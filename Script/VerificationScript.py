import matplotlib.pyplot as plt
import scipy.stats as st 
import pandas as pd 
import os

centerTypeList = ["Attraction", "Restaurant", "Entrance"]
statNameList = ["QueueTime", "ServiceTime", "Rho", "N_Q"]

def verificationCharts() :
    dataFrame : pd.DataFrame = pd.read_csv("./Out/Data/Verification/RawResults.csv", skiprows=1, header=None) 
    for centerType in centerTypeList :
        for statName in statNameList :
            createVerificationChartForCenterAndStat(dataFrame, centerType, statName)
    return

def createVerificationChartForCenterAndStat(dataFrame : pd.DataFrame, centerType : str, statName : str) :
    plt.figure(figsize=(10,5))
    axes = plt.subplot()
    for _, row in dataFrame.iterrows() :
        data = row[3 : ] 
        center : str = row[0]
        stat : str = row[1]
        
        cumAvg = data.expanding().mean()
        if (center.startswith(centerType) and stat == statName) :
            axes.plot(cumAvg, label = center)
            theoryValue : float = row[2]
    
    axes.set_xlabel("Batch Num")
    axes.set_ylabel(statName + " - Cumulative Avg")
    axes.set_title("Convergence - " + statName + " - " + centerType)
    axes.axhline(y = theoryValue, color='black', linestyle='--', label='TheoryValue')

    plt.tight_layout()
    plt.legend()
    plt.savefig("./Out/Charts/Verification/Convergence/" + centerType + "/" + centerType + "_" + statName + "_" + "Convergence")
    plt.clf()

############################################################################################################################

def confidenceIntervalCharts() :
    dataFrame : pd.DataFrame = pd.read_csv("./Out/Data/Verification/ConfidenceIntervals.csv") 
    for centerType in centerTypeList :
            for statName in statNameList :
                createVerificationChartForConfidenceInterval(dataFrame, centerType, statName)
    return

def createVerificationChartForConfidenceInterval(dataFrame : pd.DataFrame, centerType : str, statName : str) :
    filteredDf = dataFrame[(dataFrame["Center Name"].str.startswith(centerType)) & (dataFrame["Metric Name"] == statName)]
    theoryValue = filteredDf["Theory Value"].iloc[0]

    plt.figure(figsize=(6,4))
    axes = plt.subplot()
    axes.axhline(y = theoryValue, color='black', linestyle='--', label='TheoryValue')
    
    index = 0 
    xLabels = []
    for centerName in filteredDf["Center Name"] :
        mean = filteredDf[filteredDf["Center Name"] == centerName]["Mean Value"]
        lower = filteredDf[filteredDf["Center Name"] == centerName]["Lower Bound"]
        upper = filteredDf[filteredDf["Center Name"] == centerName]["Upper Bound"]

        plot = axes.scatter([index, index, index], [lower, mean, upper], s = 25)
        color = plot.get_facecolors()
        axes.vlines(x = index, ymin = lower, ymax = upper, colors=color)

        index += 1
        xLabels.append(centerName)
    
    axes.set_xlabel("Center Name")
    axes.set_xticks(ticks = [x for x in range(0, len(xLabels))], labels=xLabels, rotation = 30)

    axes.set_ylabel(statName + " - Confidence Intervals")

    axes.set_title("99% Confidence Intervals - " + statName + " - " + centerType)
   

    plt.tight_layout()
    plt.legend()
    plt.savefig("./Out/Charts/Verification/Intervals/" + centerType + "/" + centerType + "_" + statName + "_" + "ConfidenceIntervals")
    plt.clf()

############################################################################################################################

# def processVerificationData() :
#     dataFrame = pd.read_csv("./Out/Data/Verification/RawResults.csv", skiprows=1, header=None)

#     for _, row in dataFrame.iterrows() :
#         data = row[3 : ] 
#         center : str = row[0]
#         stat : str = row[1]
#         theory : float = row[2]

#         interval = st.t.interval(
#             confidence = 0.99,
#             df = len(data) - 1,
#             loc = data.mean(),
#             scale = data.var(ddof=1)
#         )
#         print("Center > ", center, "Stat > " + stat, interval)

#     return


if __name__ == "__main__" :
    for centerType in centerTypeList :
        os.makedirs("./Out/Charts/Verification/Convergence/" + centerType + "/", exist_ok = True)
        os.makedirs("./Out/Charts/Verification/Intervals/" + centerType + "/", exist_ok = True)
    verificationCharts() 
    confidenceIntervalCharts()