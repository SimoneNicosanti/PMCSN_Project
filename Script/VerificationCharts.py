import matplotlib.pyplot as plt
import pandas as pd 

def verificationCharts() :
    dataFrame : pd.DataFrame = pd.read_csv("./Out/Data/Verification/CumulativeAvgs.csv", skiprows=1, header=None) 

    createVerificationChartForCenterAndStat(dataFrame, "Attraction", "QueueTime")
    createVerificationChartForCenterAndStat(dataFrame, "Attraction", "ServiceTime")
    createVerificationChartForCenterAndStat(dataFrame, "Restaurant", "QueueTime")
    createVerificationChartForCenterAndStat(dataFrame, "Restaurant", "ServiceTime")
        


def createVerificationChartForCenterAndStat(dataFrame : pd.DataFrame, centerType : str, statName : str) :
    plt.figure(figsize=(15,5))
    axes = plt.subplot()
    for _, row in dataFrame.iterrows() :
        data = row[3 : ] 
        center : str = row[0]
        stat : str = row[1]
        

        if (center.startswith(centerType) and stat == statName) :
            axes.plot(data, label = center)#, marker = "o", markersize = "3.5")
            theoryValue : float = row[2]
    
    axes.set_xlabel("Batch Num")
    axes.set_ylabel(statName + " - Cumulative Avg")
    axes.set_title("Convergange " + statName + " - " + centerType)
    axes.axhline(y = theoryValue, color='black', linestyle='--', label='TheoryValue')

    plt.tight_layout()
    plt.legend()
    plt.savefig("./Out/Charts/Verification/" + centerType + "_" + statName + "_" + "Convergance")
    plt.clf()

if __name__ == "__main__" :
    verificationCharts() 