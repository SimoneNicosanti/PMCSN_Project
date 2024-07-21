package it.uniroma2.pmcsn.parks.engineering;

public class CenterManager {

    private Map<String, Center> centerMap;

    public CenterManager() {
        centerMap = new HashMap<>();
    }

    public void addCenterList(List<Center> centerList) {

        for(Center: center in centerList) {
            this.centerMap.put(center.name, );
        }
    }

}