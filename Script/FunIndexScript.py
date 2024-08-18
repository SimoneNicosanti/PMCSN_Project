import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

metricList = ["FunIndex", "AvgVisits", "AvgServiceTime", "AvgQueueTime"]

def funIndexChart() :
    plt.figure(figsize=(10,5))
    dataFrame : pd.DataFrame = pd.read_csv("./Out/Data/Fun/FunIndex.csv")
    groupedDataFrame = dataFrame.groupby("Priority")
    for metricName in metricList :
        for name, group in groupedDataFrame:
            # data = (group[metricName] - group[metricName].min()) / (group[metricName].max() - group[metricName].min())
            data = np.log10(group[metricName] + 1)
            plt.plot(group["Percentage"], data, label=f'Gruppo {name}', marker = "o")

            plt.xticks(ticks = np.arange(0, 1.05, 0.05))
            plt.xlabel(xlabel = "Priority Seats Percentage")
            plt.ylabel(ylabel = "Log_10 " + metricName)

            plt.tight_layout()
            plt.legend()
            plt.savefig("./Out/Charts/Fun/" + metricName + ".png")
        
        plt.clf()


def priorityQueueTimeChart() :
    plt.figure(figsize=(10,5))
    dataFrame : pd.DataFrame = pd.read_csv("./Out/Data/Fun/PriorityQueueTime.csv")
    groupedDataFrame = dataFrame.groupby(["CenterName"])
    priorities = dataFrame["Priority"].unique()
    print(priorities)
    for name, group in groupedDataFrame:
        for priority in priorities :
            data = group[group["Priority"] == priority]
            plt.plot(data["Percentage"], data["AvgQueueTime"], label=f'{priority}', marker = "o")

        plt.xticks(ticks = np.arange(0, 1.05, 0.05))
        plt.xlabel(xlabel = "Priority Seats Percentage")
        plt.ylabel(ylabel = "AvgQueueTime")

        plt.tight_layout()
        plt.legend()
        plt.savefig("./Out/Charts/Fun/" + "PriorityQueueTime_" + name[0] + ".png")
        plt.clf()


if __name__ == "__main__" :
    os.makedirs("./Out/Charts/Fun/", exist_ok = True)
    funIndexChart() 
    priorityQueueTimeChart()