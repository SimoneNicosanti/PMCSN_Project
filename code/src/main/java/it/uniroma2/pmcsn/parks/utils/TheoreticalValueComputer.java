package it.uniroma2.pmcsn.parks.utils;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.model.RoutingNodeType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.AbstractCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Entrance;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Restaurant;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class TheoreticalValueComputer {

    private Double lambda_r;
    private Double lambda_a;
    private Double lambda;

    public TheoreticalValueComputer() {
        this.lambda = ConfigHandler.getInstance().getCurrentArrivalRate();
        Double p_a = ConfigHandler.getInstance().getProbability(RoutingNodeType.ATTRACTION);
        Double p_r = ConfigHandler.getInstance().getProbability(RoutingNodeType.RESTAURANT);
        Double k_a = (1 / p_a) - 1;
        Double t_a = 1 + 1 / k_a;
        this.lambda_r = this.lambda * ((t_a * p_r) / (1 - t_a * p_r));
        this.lambda_a = (this.lambda + this.lambda_r) / k_a;
    }

    public Map<String, Map<String, Double>> computeAllTheoreticalValues(List<Center<RiderGroup>> centerList) {

        int attractionNumber = 0;
        int restaurantNumber = 0;
        for (Center<RiderGroup> center : centerList) {
            if (((StatsCenter) center).getCenter() instanceof Restaurant) {
                restaurantNumber++;
            } else if (((StatsCenter) center).getCenter() instanceof Attraction) {
                attractionNumber++;
            }
        }

        Map<String, Double> serviceTimeMap = computeTheoreticalServiceTimeMap(centerList);
        Map<String, Double> queueTimeMap = computeTheoreticalQueueTimeMap(centerList, attractionNumber,
                restaurantNumber);
        // Map<String, Double> responseTimeMap =
        // computeTheoreticalResponseTime(serviceTimeMap, queueTimeMap);
        Map<String, Double> numberInQueueMap = computeTheoreticalNumberInQueue(queueTimeMap, centerList,
                attractionNumber,
                restaurantNumber);

        Map<String, Double> rhoMap = computeTheoreticalRho(centerList, attractionNumber, restaurantNumber);

        Map<String, Map<String, Double>> returnMap = new HashMap<>();
        for (Center<RiderGroup> center : centerList) {
            returnMap.put(center.getName(), new HashMap<>());
        }
        for (String centerName : serviceTimeMap.keySet()) {
            returnMap.get(centerName).put("ServiceTime", serviceTimeMap.get(centerName));
        }
        for (String centerName : queueTimeMap.keySet()) {
            returnMap.get(centerName).put("QueueTime", queueTimeMap.get(centerName));
        }
        for (String centerName : serviceTimeMap.keySet()) {
            returnMap.get(centerName).put("Rho", rhoMap.get(centerName));
        }
        for (String centerName : numberInQueueMap.keySet()) {
            returnMap.get(centerName).put("N_Q", numberInQueueMap.get(centerName));
        }

        return returnMap;
    }

    private Map<String, Double> computeTheoreticalNumberInQueue(Map<String, Double> queueTimeMap,
            List<Center<RiderGroup>> centerList, int attractionNumber, int restaurantNumber) {

        Map<String, Double> returnMap = new HashMap<>();
        for (Center<RiderGroup> center : centerList) {
            AbstractCenter absCenter = (AbstractCenter) ((StatsCenter) center).getCenter();

            Double queueTime = queueTimeMap.get(center.getName());
            Double centerLambda = 0.0;

            if (absCenter instanceof Attraction) {
                centerLambda = this.lambda_a / attractionNumber;
            } else if (absCenter instanceof Restaurant) {
                centerLambda = this.lambda_r / restaurantNumber;
            } else if (absCenter instanceof Entrance) {
                centerLambda = this.lambda;
            } else {
                centerLambda = -1.0;
            }

            double n_q = centerLambda * queueTime;

            returnMap.put(center.getName(), n_q);
        }

        return returnMap;
    }

    private Map<String, Double> computeTheoreticalRho(List<Center<RiderGroup>> centerList, int attractionNumber,
            int restaurantNumber) {

        Map<String, Double> rhoMap = new HashMap<>();

        for (Center<RiderGroup> center : centerList) {
            AbstractCenter absCenter = (AbstractCenter) ((StatsCenter) center).getCenter();
            Double centerLambda = 0.0;

            if (absCenter instanceof Attraction) {
                centerLambda = this.lambda_a / attractionNumber;
            } else if (absCenter instanceof Restaurant) {
                centerLambda = this.lambda_r / restaurantNumber;
            } else if (absCenter instanceof Entrance) {
                centerLambda = this.lambda;
            } else {
                centerLambda = -1.0;
            }

            int m = absCenter.getSlotNumber();
            double e_s_i = absCenter.getAvgDuration();
            double rho = centerLambda * e_s_i / m;

            rhoMap.put(center.getName(), rho);
        }

        return rhoMap;
    }

    private Map<String, Double> computeTheoreticalServiceTimeMap(List<Center<RiderGroup>> centerList) {
        Map<String, Double> serviceTimeMap = new HashMap<>();
        for (Center<RiderGroup> center : centerList) {
            serviceTimeMap.put(center.getName(),
                    ((AbstractCenter) ((StatsCenter) center).getCenter()).getAvgDuration());
        }
        return serviceTimeMap;
    }

    private Map<String, Double> computeTheoreticalQueueTimeMap(List<Center<RiderGroup>> centerList,
            int attractionNumber, int restaurantNumber) {

        Map<String, Double> returnMap = new HashMap<>();

        for (Center<RiderGroup> center : centerList) {
            AbstractCenter absCenter = (AbstractCenter) ((StatsCenter) center).getCenter();
            Double centerQueueTime;

            if (absCenter instanceof Attraction) {
                Double centerLambda = this.lambda_a / attractionNumber;
                centerQueueTime = computeCenterTheoreticalQueueTime(centerLambda, absCenter);
            } else if (absCenter instanceof Restaurant) {
                Double centerLambda = this.lambda_r / restaurantNumber;
                centerQueueTime = computeCenterTheoreticalQueueTime(centerLambda, absCenter);
            } else if (absCenter instanceof Entrance) {
                centerQueueTime = computeCenterTheoreticalQueueTime(lambda, absCenter);
            } else {
                centerQueueTime = -1.0;
            }

            returnMap.put(center.getName(), centerQueueTime);
        }

        return returnMap;
    }

    private static double computeCenterTheoreticalQueueTime(Double centerLambda, AbstractCenter absCenter) {

        int m = absCenter.getSlotNumber();
        double e_s_i = absCenter.getAvgDuration();
        double e_s = e_s_i / m;
        double rho = centerLambda * e_s_i / m;
        if (rho >= 1) {
            throw new RuntimeException("Center Utilization Greater Than 1!! Rho Value = " + rho);
        }

        double p_0 = computeP_0(m, rho);
        double p_q = (Math.pow(rho * m, m) / (CombinatoricsUtils.factorial(m) * (1 - rho))) * p_0;

        return p_q * e_s / (1 - rho);
    }

    private static double computeP_0(int m, double rho) {
        double sum = 0.0;
        for (int k = 0; k <= m - 1; k++) {
            sum += Math.pow(rho * m, k) / CombinatoricsUtils.factorial(k);
        }
        sum += Math.pow(rho * m, m) / (CombinatoricsUtils.factorial(m) * (1 - rho));

        return 1 / sum;
    }
}
