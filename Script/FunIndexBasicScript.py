import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

metricList = ["FunIndex"]  # , "AvgVisits", "AvgServiceTime", "AvgQueueTime"]
fileIdx = [1, 2]

def funIndexChart():
    plt.figure(figsize=(10, 5))
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
    groupedDataFrame = dataFrame.groupby("Priority")
    # normalDataFrame = dataFrame[dataFrame["Priority"] == "Normal"]
    # priorityDataFrame = dataFrame[dataFrame["Priority"] == "Priority"]
    # smallDataFrame = dataFrame[dataFrame["Priority"] == "Small"]

    # dataList = []

    # if not showSmallGroup:
    #     normalDataFrame = normalDataFrame + smallDataFrame
    # else:
    #     dataList.append(("Small", smallDataFrame))

    # dataList.append(("Normal", normalDataFrame))


    for metricName in metricList:


        for name, group in groupedDataFrame:
            # data = (group[metricName] - group[metricName].min()) / (group[metricName].max() - group[metricName].min())

            data = group[metricName]
            plt.plot(
                group["PrioSeatsPercentage"],
                np.log10(data),
                label=f"Gruppo {name}",
                marker="o",
            )
            plt.fill_between(
                group["PrioSeatsPercentage"],
                np.log10(data - group["ConfInterval"]),
                np.log10(data + group["ConfInterval"]),
                alpha=0.2,
            )

            plt.xticks(ticks=np.arange(0, 1.05, 0.1))
            plt.xlabel(xlabel="Priority Seats Percentage")
            plt.ylabel(ylabel="Log_10 " + metricName)

            plt.title("FunIndex Trend")

            plt.tight_layout()
            plt.legend()
            plt.savefig(
                "./Out/Charts/Fun/Basic/Small_" + str(idx) + "/" + metricName + ".png"
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
