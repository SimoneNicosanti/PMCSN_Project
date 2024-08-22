import pandas as pd
import matplotlib.pyplot as plt
import os

centerTypeList = ["Attraction", "Restaurant"]

def validationCharts() :
    dataFrame = pd.read_csv("./Out/Data/Validation/Consistency/RawList_0.csv", skiprows=1, header=None)
    for centerType in centerTypeList :
        drawChart(dataFrame, centerType, 0)

    dataFrame = pd.read_csv("./Out/Data/Validation/Consistency/RawList_1.csv", skiprows=1, header=None)
    for centerType in centerTypeList :
        drawChart(dataFrame, centerType, 1)

def drawChart(dataFrame : pd.DataFrame, centerType : str, fileIdx : int) :
    plt.figure(figsize=(10,5))
    axes = plt.subplot()
    for _, row in dataFrame.iterrows() :
        data = row[1 : ] 
        center : str = row[0]
        
        cumAvg = data.expanding().mean()
        if (center.startswith(centerType) ) :
            axes.plot(cumAvg, label = center)
    
    axes.set_xlabel("Batch Num")
    axes.set_ylabel(centerType + " - Cumulative Avg")
    axes.set_title("E[Tq] Trend - " + centerType)
    #axes.axhline(y = theoryValue, color='black', linestyle='--', label='TheoryValue')

    plt.tight_layout()
    plt.legend(loc='upper right')
    plt.savefig("./Out/Charts/Validation/Trends/" + centerType  + "_Trend_" + str(fileIdx))
    plt.clf()
    pass


if __name__ == "__main__" :
    os.makedirs("./Out/Charts/Validation/Trends/", exist_ok = True)
    validationCharts()