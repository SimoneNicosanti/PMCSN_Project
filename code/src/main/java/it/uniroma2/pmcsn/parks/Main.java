package it.uniroma2.pmcsn.parks;

import it.uniroma2.pmcsn.parks.controller.ParkController;
import it.uniroma2.pmcsn.parks.utils.WriterHelper;

public class Main {
    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new ParkController().simulate();
    }
}