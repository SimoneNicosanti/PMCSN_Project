import pandas as pd
import matplotlib.pyplot as plt
import os
import numpy as np

FILE_PATH = '.Out/Data/Intervals/QueueTime.csv'
OUT_DIR = './Out/Charts/Intervals/'

def plot_center_data(df: pd.DataFrame, center_name: str, output_dir: str):
    """Plot queue time charts for a specific center."""
    plt.figure(figsize=(12, 8))
    
    groupedDataframeByPrio = df.groupby('Priority')

    # Plot data for each priority
    for queueData in groupedDataframeByPrio:
        plot_queue_time(queueData['IntervalIndex'], queueData['Priority'], queueData['AvgQueueTime'], queueData['ConfInterval'])
                
    
    # Customize and save the plot
    customize_and_save_plot(center_name, output_dir)
    plt.close()  # Close the figure to avoid display in interactive environments


def filter_data(group: pd.DataFrame, time_interval, priority) -> pd.DataFrame:
    """Filter data based on time interval and priority."""
    return group[(group['TimeInterval'] == time_interval) & 
                 (group['Priority'] == priority)]


def plot_queue_time(intervalIdx, prio, avgQueueTime, confInterval):
    """Plot the queue time for a given time interval and priority."""
    plt.plot(
        intervalIdx,
        avgQueueTime,
        label=f'Priority: {prio}',
        marker='o'
    )
    
    plt.fill_between(
        intervalIdx,
        avgQueueTime - confInterval,
        avgQueueTime + confInterval,
        alpha=0.2
    )


def customize_and_save_plot(center_name: str, output_dir: str):
    """Customize the plot and save it as a PNG file."""
    plt.xlabel('Interval Index')
    plt.ylabel('Average Queue Time')
    plt.title(f'{center_name} - Queue Time Analysis')
    plt.legend()
    plt.tight_layout()
    
    # Save the figure to a file
    plt.savefig(os.path.join(output_dir, f'{center_name}_QueueTimeChart.png'))


def process_data(file_path: str, output_dir: str):
    """Main processing function to generate charts for each center."""
    # Load the data
    dataFrame = pd.read_csv(file_path)
    
    # Group data by 'CenterName'
    groupedDataFrame = dataFrame.groupby('CenterName')
    
    # Iterate over each center and generate charts
    for center_name, group in groupedDataFrame:
        plot_center_data(group, center_name, output_dir)


def main():
    # Create output directory
    os.makedirs(OUT_DIR, exist_ok=True)
    
    # Process the data and generate charts
    process_data(FILE_PATH, OUT_DIR)


if __name__ == "__main__":
    main()
