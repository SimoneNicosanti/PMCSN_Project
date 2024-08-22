import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

metricList = ["FunIndex"]  # , "AvgVisits", "AvgServiceTime", "AvgQueueTime"]
fileIdx = [1, 2]


def priorityQueueTimeChart():
    plt.figure(figsize=(10, 5))
    for idx in fileIdx:
        try:
            dataFrame: pd.DataFrame = pd.read_csv(
                "./Out/Data/Fun/Basic/PriorityQueueTime_" + str(idx) + ".csv"
            )
            plotQueueTimeChart(dataFrame, idx)
        except FileNotFoundError:
            print(f"File not found for index {idx}")
            pass


def plotQueueTimeChart(dataFrame: pd.DataFrame, idx: int):
    groupedDataFrame = dataFrame.groupby(["CenterName"])
    priorities = dataFrame["Priority"].unique()
    for prioSeatsPerc in dataFrame["PrioSeatsPercentage"].unique() :
        fig, axes = plt.subplots(nrows = 1, ncols = 2 , figsize = (16,5))
        i = 0 
        for priority in priorities:
            for name, group in groupedDataFrame:
                    data = group[(group["Priority"] == priority) & (group["PrioSeatsPercentage"] == prioSeatsPerc)]
                    axes[i].plot(
                        data["PrioPassProb"],
                        data["AvgQueueTime"],
                        label=f"{name[0]}",
                        marker="o",
                    )

                    axes[i].fill_between(
                        data["PrioPassProb"],
                        data["AvgQueueTime"] - data["ConfInterval"],
                        data["AvgQueueTime"] + data["ConfInterval"],
                        alpha=0.2,
                    )

            if (priority == "PRIORITY") :
                axes[i].axhline(y=30, color="black", linestyle="--", label="QoS")

            axes[i].set_xlabel("Priority Pass Probability")
            axes[i].set_ylabel("Avg Queue Time")
            axes[i].set_title(label = f"Priority = {priority} - Prio Seats Percentage = {prioSeatsPerc} - Avg E[Tq]")
            
            if (i == 0) :
                axes[i].legend()
            i += 1

            

        fig.tight_layout()

        plt.savefig(
            "./Out/Charts/ArrivalPrioPerc/Basic/ArrivalPrioPerc_" + str(prioSeatsPerc) + ".png"
        )
        plt.clf()
            


if __name__ == "__main__":
    os.makedirs("./Out/Charts/ArrivalPrioPerc/Basic/", exist_ok=True)
    for idx in fileIdx:
        os.makedirs("./Out/Charts/ArrivalPrioPerc/Basic/", exist_ok=True)
    priorityQueueTimeChart()
