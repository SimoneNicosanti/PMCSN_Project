import pandas as pd

def funFunction() :
    dataFrame = pd.read_csv("./Out/Data/Job/job_stats.csv")

    dataFrame = dataFrame[dataFrame["NumberRides"] > 0]
    dataFrame["FunIndex"] = (dataFrame["RidingTime"] * dataFrame["NumberRides"]) / (dataFrame["QueueTime"] + 1)

    meanFun = dataFrame["FunIndex"].mean()

    priorityDataFrame = dataFrame[dataFrame["Priority"] == "PRIORITY"]
    normalDataFrame = dataFrame[dataFrame["Priority"] == "NORMAL"]
    singleDataFrame = normalDataFrame[dataFrame["GroupSize"] == 1]
    coupleDataFrame = normalDataFrame[dataFrame["GroupSize"] == 2]

    priorityFun = priorityDataFrame["FunIndex"].mean()
    normalFun = normalDataFrame["FunIndex"].mean()
    singleFun = singleDataFrame["FunIndex"].mean()
    coupleFun = coupleDataFrame["FunIndex"].mean()

    singleQueue = singleDataFrame["QueueTime"].mean()
    singleNumber = singleDataFrame["QueueTime"].size
    coupleNumber = coupleDataFrame["QueueTime"].size
    priorityQueue = priorityDataFrame["QueueTime"].mean()


    print()
    print("Total Fun >>> ", meanFun)
    print("Normal Fun >>> ", normalFun)
    print("Priority Fun >>> ", priorityFun)
    print("Priority Queue >>> ", priorityQueue)
    print("Single Fun >>> ", singleFun)
    print("Single Queue >>> ", singleQueue)

    print("Single Num >>> ", singleNumber)
    print("Couple Num >>> ", coupleNumber)

    print("Couple Fun >>> ", coupleFun)
    print()

    return



if __name__ == "__main__" :
    funFunction() 