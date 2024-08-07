package it.uniroma2.pmcsn.parks;

import it.uniroma2.pmcsn.parks.controller.VerifyController;
import it.uniroma2.pmcsn.parks.utils.WriterHelper;

public class VerifyMain {

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        new VerifyController().simulate();
    }

}
