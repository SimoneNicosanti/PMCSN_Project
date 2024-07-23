package it.uniroma2.pmcsn.parks.engineering.factory;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import it.uniroma2.pmcsn.parks.model.server.Restaurant;

public class RestaurantBuilder {

    public List<Restaurant> buildFromFile(String fileName) {

        List<Restaurant> restaurantList = new ArrayList<>();

        try (
                FileReader filereader = new FileReader(fileName);
                CSVReader csvReader = new CSVReader(filereader);) {

            String[] nextRecord;

            // Skip header
            csvReader.readNext();

            // Read line by line - (Name, Popularity, AvgDuration)
            while ((nextRecord = csvReader.readNext()) != null) {
                String restaurantName = nextRecord[0];
                double restaurantPopularity = Double.valueOf(nextRecord[1]);
                double restaurantAvgDuration = Double.valueOf(nextRecord[2]);

                restaurantList.add(buildRestaurant(restaurantName, restaurantPopularity, restaurantAvgDuration));
                // TODO Build Attraction class
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return restaurantList;
    }

    private Restaurant buildRestaurant(String name, double popularity, double avgDuration) {
        // TODO Check how to add the number of seats
        return new Restaurant(name, 0, popularity, avgDuration);

    }

}
