package com.example.demo.jython;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.FactoryBean;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;

public class PythonServiceFactory implements FactoryBean<PythonService> {
    private PyObject pyObject;
    private PythonService defaultService;
    @Override
    public PythonService getObject() throws Exception {
        /*The python classpath is usually set by environment variable
         * or included in the java project classpath but it can also be set
         * programmatically.  Here I hard code it just for the example.
         * This is not required if we use jython standalone JAR
         */
        PySystemState systemState = Py.getSystemState();
        systemState.path.append(new PyString("C:\\jython2.7.0\\Lib"));

        //Here is the actual code that interprets our python file.
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile("src\\main\\java\\com\\example\\demo\\jython\\PythonServicePython.py");
        pyObject = interpreter.get("PythonServicePython");
        defaultService = (PythonService) pyObject.__call__(new PyLong(2L)).__tojava__(PythonService.class);
//Cast the created object to our Java interface
        return defaultService;
    }

    public PythonService create(Path filePath) {
        try {
            byte[] tmp = Files.readAllBytes(filePath);
            String out = "";
            for (int i = 0; i < 10; i++) {
                out = out.concat(Array.get(tmp, i).toString());
            }
            out=out.replace("-", "");

            PyObject serviceObject = pyObject.__call__(new PyLong(Long.parseLong(out)));
            return (PythonService) serviceObject.__tojava__(PythonService.class);
        } catch (IOException e) {
            return defaultService;
         }
    }
    public String getRating(Path filePath) {
        if (filePath.toString().isEmpty()) {
            return "";
        }
        PythonService service = create(filePath);
        return service.getRating().toString();
    }

    @Override
    public Class<?> getObjectType() {
        return PythonService.class;
    }
}
