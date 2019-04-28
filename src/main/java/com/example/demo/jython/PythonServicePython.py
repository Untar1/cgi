from com.example.demo.jython import PythonService

from java.util import Random

class PythonServicePython(PythonService):
    def __init__(self, predSeed):
        self.predSeed=predSeed
    def getRating(self):
        #print("Or filepath is: " + self.predSeed)
        random = Random(self.predSeed)
        #random.seed(self.predSeed)
        value = random.nextInt(10)
        return value
