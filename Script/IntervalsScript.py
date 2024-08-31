import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import os
import numpy as np

FILE_PATH = "./Out/Data/Intervals/QueueTime.csv"
OUT_DIR = "./Out/Charts/Intervals/"


def plot_data():
    df = pd.read_csv(FILE_PATH)

    groupedDataFrame = df.groupby(["CenterName"])
    priorities = df["Priority"].unique()
    for centerName, group in groupedDataFrame:
        plt.figure(figsize=(12, 8))
        plt.axhline(y=30, color="black", linestyle="--", label="QoS")
        for prio in priorities:
            data = group[group["Priority"] == prio].sort_values(by="IntervalIndex")
            plot_queue_time(
                data["IntervalIndex"].astype(int),
                prio,
                data["AvgQueueTime"],
                data["ConfInterval"],
            )
        data = group[group["Priority"] == prio].sort_values(by = "IntervalIndex")

        customize_and_save_plot(centerName[0], OUT_DIR, len(data["IntervalIndex"]))

        plt.close()  # Close the figure to avoid display in interactive environments


def plot_queue_time(intervalIdx, prio, avgQueueTime, confInterval):
    """Plot the queue time for a given time interval and priority."""
    if (len(avgQueueTime) > 10) :
        marker = None
        plt.axvline(180, color="green", linestyle="--")
        plt.axvline(300, color="green", linestyle="--")
        plt.axvline(600, color="green", linestyle="--")
    else :
        marker = "o"
    plt.plot(intervalIdx, avgQueueTime, label=f"Priority: {prio}", marker=marker)

    plt.fill_between(
        intervalIdx, avgQueueTime - confInterval, avgQueueTime + confInterval, alpha=0.2
    )


def customize_and_save_plot(center_name: str, output_dir: str, size : int):
    """Customize the plot and save it as a PNG file."""
    if (size > 10) :
        xLabel = "Time"
    else :
        xLabel = "Interval Index"
    
    plt.xlabel(xLabel)
    plt.ylabel("Average Queue Time")
    plt.title(f"{center_name} - Queue Time Analysis")
    plt.legend()
    plt.gca().xaxis.set_major_locator(ticker.MaxNLocator(integer=True))
    plt.tight_layout()

    # Save the figure to a file
    plt.savefig(os.path.join(output_dir, f"{center_name}_QueueTimeChart.png"))


if __name__ == "__main__":
    # Create output directory
    os.makedirs(OUT_DIR, exist_ok=True)
    files = os.listdir(OUT_DIR)
    for file in files:
        os.remove(os.path.join(OUT_DIR, file))

    plot_data()
