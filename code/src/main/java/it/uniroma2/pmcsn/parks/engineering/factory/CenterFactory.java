package it.uniroma2.pmcsn.parks.engineering.factory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import it.uniroma2.pmcsn.parks.model.server.concreate_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.server.concreate_servers.Entrance;
import it.uniroma2.pmcsn.parks.model.server.concreate_servers.Restaurant;

public class CenterFactory {

    public static Restaurant buildRestaurant(String name, int numberOfSeats, double popularity,
            double avgDuration) {
        return new Restaurant(name, numberOfSeats, popularity, avgDuration);
    }

    public static Attraction buildAttraction(String name, int numberOfSeats, double popularity,
            double avgDuration) {
        return new Attraction(name, numberOfSeats, popularity, avgDuration);
    }

    public static Entrance buildEntrance(String name, int numberOfSeats, double avgDuration) {
        return new Entrance(name, numberOfSeats, avgDuration);
    }

    public static List<Attraction> buildAttractionsFromFile(String fileName) {
        List<CSVRecord> recordsList = readAllRecords(fileName);
        List<Attraction> attractionList = new ArrayList<>();
        for (CSVRecord record : recordsList) {
            String name = record.get("Name");
            Double avgDuration = Double.parseDouble(record.get("AvgDuration"));
            Integer totalSeats = Integer.parseInt(record.get("TotalSeats"));
            Double popularity = Double.parseDouble(record.get("Popularity"));

            attractionList.add(buildAttraction(name, totalSeats, popularity, avgDuration));
        }

        return attractionList;
    }

    public static List<Restaurant> buildRestaurantsFromFile(String fileName) {
        List<CSVRecord> recordsList = readAllRecords(fileName);
        List<Restaurant> restaurantList = new ArrayList<>();
        for (CSVRecord record : recordsList) {
            String name = record.get("Name");
            Double avgDuration = Double.parseDouble(record.get("AvgDuration"));
            Integer totalSeats = Integer.parseInt(record.get("TotalSeats"));
            Double popularity = Double.parseDouble(record.get("Popularity"));
            restaurantList.add(buildRestaurant(name, totalSeats, popularity, avgDuration));
        }

        return restaurantList;

    }

    public static List<Entrance> buildEntranceFromFile(String fileName) {
        List<CSVRecord> recordsList = readAllRecords(fileName);
        List<Entrance> entranceList = new ArrayList<>();
        for (CSVRecord record : recordsList) {
            String name = record.get("Name");
            Double avgDuration = Double.parseDouble(record.get("AvgDuration"));
            Integer totalSeats = Integer.parseInt(record.get("TotalSeats"));

            entranceList.add(buildEntrance(name, totalSeats, avgDuration));
        }

        return entranceList;
    }

    private static List<CSVRecord> readAllRecords(String fileName) {
        Path filePath = Path.of("config", fileName);

        List<CSVRecord> recordsList = new ArrayList<>();

        try (
                Reader reader = new FileReader(filePath.toString());
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                recordsList.add(record);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recordsList;
    }
}
