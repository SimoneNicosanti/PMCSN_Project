# Rewriting the script to perform the data extraction and save the CSV in a more controlled environment.

import requests
from lxml import html
import pandas as pd

urlsDict = {
    "The Amazing Adventures of Spider-Man" : "https://queue-times.com/en-US/parks/64/rides/5985",
    "Caro-Seuss-el" : "https://queue-times.com/en-US/parks/64/rides/5986",
    "The Cat in the Hat" : "https://queue-times.com/en-US/parks/64/rides/5987",
    "Doctor Doom's Fearfall" : "https://queue-times.com/en-US/parks/64/rides/5988",
    "Dudley Do-Right's Ripsaw Falls" : "https://queue-times.com/en-US/parks/64/rides/5989",
    "Flight of the Hippogriff" : "https://queue-times.com/en-US/parks/64/rides/5991",
    "Hagrid's Magical Creatures Motorbike Adventure" : "https://queue-times.com/en-US/parks/64/rides/6682",
    "Harry Potter and the Forbidden Journey" : "https://queue-times.com/en-US/parks/64/rides/5992",
    "The Incredible Hulk Coaster" : "https://queue-times.com/en-US/parks/64/rides/6004",
    "Jurassic Park River Adventure" : "https://queue-times.com/en-US/parks/64/rides/5994",
    "Jurassic World VelociCoaster" : "https://queue-times.com/en-US/parks/64/rides/8721",
    "Popeye & Bluto's Bilge-Rat Barges" : "https://queue-times.com/en-US/parks/64/rides/5998",
    "Pteranodon Flyers" : "https://queue-times.com/en-US/parks/64/rides/5999",
    "Skull Island: Reign of Kong" : "https://queue-times.com/en-US/parks/64/rides/6017",
    "Storm Force Accelatron" : "https://queue-times.com/en-US/parks/64/rides/6003"
}


data = []

# Function to extract data from a given URL
def extract_data(name, url):
    response = requests.get(url)
    webpage = html.fromstring(response.content)
    
    # Extract the name of the attraction
    #name = webpage.xpath("/html/body/section/div[1]/div[2]/div[1]/div[1]/h1/text()")[0].strip()
    
    # Extract the table data
    rows = webpage.xpath("/html/body/section/div[1]/div[2]/div[2]/div[4]/div[2]/div[1]/div[3]/table/tbody/tr")
    for row in rows:
        hour = row.xpath("td[1]/text()")[0].strip()
        avg_wait_time = row.xpath("td[2]/span/text()")[0].strip()
        data.append([name, hour, avg_wait_time])


def main() :
    # Extract data for each URL
    for name in urlsDict.keys():
        extract_data(name, urlsDict[name])

    # Convert the data to a DataFrame and save it as a CSV
    df = pd.DataFrame(data, columns=["Attraction", "Hour", "Average Wait Time"])
    csv_file_path = "./attraction_wait_times.csv"
    df.to_csv(csv_file_path, index=False)


if __name__ == "__main__" :
    main() 