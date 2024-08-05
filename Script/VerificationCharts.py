import matplotlib.pyplot as plt
import pandas as pd 
import os

def verificationCharts() :
    dataFrame : pd.DataFrame = pd.read_csv("./Out/Data/Verification/CumulativeAvgs.csv", skiprows=1, header=None) 
    createVerificationChartForCenterAndStat(dataFrame, "Attraction", "QueueTime")
    createVerificationChartForCenterAndStat(dataFrame, "Attraction", "ServiceTime")
    createVerificationChartForCenterAndStat(dataFrame, "Restaurant", "QueueTime")
    createVerificationChartForCenterAndStat(dataFrame, "Restaurant", "ServiceTime")
    return

def createVerificationChartForCenterAndStat(dataFrame : pd.DataFrame, centerType : str, statName : str) :
    plt.figure(figsize=(16,5))
    axes = plt.subplot()
    for _, row in dataFrame.iterrows() :
        data = row[3 : ] 
        center : str = row[0]
        stat : str = row[1]
        
        if (center.startswith(centerType) and stat == statName) :
            axes.plot(data, label = center, marker = "o", markersize = "3.5")
            theoryValue : float = row[2]
    
    axes.set_xlabel("Batch Num")
    axes.set_ylabel(statName + " - Cumulative Avg")
    axes.set_title("Convergence - " + statName + " - " + centerType)
    axes.axhline(y = theoryValue, color='black', linestyle='--', label='TheoryValue')

    plt.tight_layout()
    plt.legend()
    plt.savefig("./Out/Charts/Verification/Convergence/" + centerType + "_" + statName + "_" + "Convergence")
    plt.clf()

############################################################################################################################à

def confidenceIntervalCharts() :
    dataFrame : pd.DataFrame = pd.read_csv("./Out/Data/Verification/ConfidenceIntervals.csv") 
    createVerificationChartForConfidenceInterval(dataFrame, "Attraction", "QueueTime")
    createVerificationChartForConfidenceInterval(dataFrame, "Attraction", "ServiceTime")
    createVerificationChartForConfidenceInterval(dataFrame, "Restaurant", "QueueTime")
    createVerificationChartForConfidenceInterval(dataFrame, "Restaurant", "ServiceTime")
    return

def createVerificationChartForConfidenceInterval(dataFrame : pd.DataFrame, centerType : str, statName : str) :
    filteredDf = dataFrame[(dataFrame["Center Name"].str.startswith(centerType)) & (dataFrame["Metric Name"] == statName)]
    theoryValue = filteredDf["Theory Value"].iloc[0]

    plt.figure(figsize=(15,5))
    axes = plt.subplot()
    axes.axhline(y = theoryValue, color='black', linestyle='--', label='TheoryValue')
    
    index = 0 
    for centerName in filteredDf["Center Name"] :
        mean = filteredDf[filteredDf["Center Name"] == centerName]["Mean Value"]
        lower = filteredDf[filteredDf["Center Name"] == centerName]["Lower Bound"]
        upper = filteredDf[filteredDf["Center Name"] == centerName]["Upper Bound"]

        plot = axes.scatter([index, index, index], [lower, mean, upper], s = 25)
        color = plot.get_facecolors()
        axes.vlines(x = index, ymin = lower, ymax = upper, label = centerName, colors=color)

        index += 1
    
    #axes.set_xlabel("Batch Num")
    axes.set_ylabel(statName + " - Confidence Intervals")
    axes.set_title("Confidence Intervals - " + statName + " - " + centerType)
    axes.set_xticks([])

    plt.tight_layout()
    plt.legend()
    plt.savefig("./Out/Charts/Verification/Intervals/" + centerType + "_" + statName + "_" + "ConfidenceIntervals")
    plt.clf()



if __name__ == "__main__" :
    os.makedirs("./Out/Charts/Verification/Convergence", exist_ok = True)
    os.makedirs("./Out/Charts/Verification/Intervals", exist_ok = True)
    verificationCharts() 
    confidenceIntervalCharts()