import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

metricList = ["FunIndex"]  # , "AvgVisits", "AvgServiceTime", "AvgQueueTime"]
fileIdx = [1, 2]

def funIndexChart():
    for idx in fileIdx:
        try:
            dataFrame: pd.DataFrame = pd.read_csv(
                "./Out/Data/Fun/Basic/FunIndex_" + str(idx) + ".csv"
            )
            plotFunIndexChart(dataFrame, idx)
        except FileNotFoundError:
            print(f"File not found for index {idx}")
            pass


def plotFunIndexChart(dataFrame: pd.DataFrame, idx: int, showSmallGroup : bool = False):
    groupedDataFrame = dataFrame.groupby(["Priority"])
    
    fig, axes = plt.subplots(nrows = 1, ncols = 2, figsize = (12.5,5))
    for name, group in groupedDataFrame:
        # if (name == "PRIORITY") :
        #     continue
        # data = (group[metricName] - group[metricName].min()) / (group[metricName].max() - group[metricName].min())
        data = group["FunIndex"]
        axes[0].plot(
            group["PrioSeatsPercentage"],
            data,
            label=f"Gruppo {name[0]}",
            marker="o",
        )
        axes[0].fill_between(
            group["PrioSeatsPercentage"],
            data - group["ConfInterval"],
            data + group["ConfInterval"],
            alpha=0.2,
        )

        if (name[0] != "PRIORITY") :
            axes[1].plot(group["PrioSeatsPercentage"], data, label=name[0], marker="o")
            axes[1].fill_between(
                group["PrioSeatsPercentage"],
                data - group["ConfInterval"],
                data + group["ConfInterval"],
                alpha=0.2,
            )

    for i in range(0, 2) :
        axes[i].set_xticks(ticks=np.arange(0, 1.05, 0.1))
        axes[i].set_xlabel(xlabel="Priority Seats Percentage")
        axes[i].set_ylabel(ylabel="Fun Index")

        axes[i].set_title("FunIndex Trend")
        axes[i].legend()
        print("CIAO")
        axes[i].grid()
            
    plt.tight_layout()
    plt.savefig(
        "./Out/Charts/Fun/Basic/Small_" + str(idx) + "/" + "FunIndex" + ".png"
    )
    plt.clf()


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
    for name, group in groupedDataFrame:
        for priority in priorities:
            data = group[group["Priority"] == priority]
            plt.plot(
                data["PrioSeatsPercentage"],
                data["AvgQueueTime"],
                label=f"{priority}",
                marker="o",
            )

            plt.fill_between(
                data["PrioSeatsPercentage"],
                data["AvgQueueTime"] - data["ConfInterval"],
                data["AvgQueueTime"] + data["ConfInterval"],
                alpha=0.2,
            )

        plt.xticks(ticks=np.arange(0, 1.05, 0.1))
        plt.xlabel(xlabel="Priority Seats Percentage")
        plt.ylabel(ylabel="AvgQueueTime")

        plt.axhline(y=30, color="black", linestyle="--", label="QoS")

        plt.title(label=name[0] + " - Avg E[Tq]")

        plt.tight_layout()
        plt.legend()
        plt.savefig(
            "./Out/Charts/Fun/Basic/Small_"
            + str(idx)
            + "/"
            + "PriorityQueueTime_"
            + name[0]
            + ".png"
        )
        plt.clf()


if __name__ == "__main__":
    os.makedirs("./Out/Charts/Fun/Basic/", exist_ok=True)
    for idx in fileIdx:
        os.makedirs("./Out/Charts/Fun/Basic/Small_" + str(idx) + "/", exist_ok=True)
    funIndexChart()
    priorityQueueTimeChart()
