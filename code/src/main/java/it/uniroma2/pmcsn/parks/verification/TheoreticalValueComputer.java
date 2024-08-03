package it.uniroma2.pmcsn.parks.verification;

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

import org.apache.commons.collections.map.HashedMap;
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

    public Map<String, Double> computeTheoreticalQueueTimeMap(List<Center<RiderGroup>> centerList) {

        Map<String, Double> returnMap = new HashMap<>();

        int attractionNumber = 0;
        int restaurantNumber = 0;
        for (Center<RiderGroup> center : centerList) {
            if (((StatsCenter) center).getCenter() instanceof Restaurant) {
                restaurantNumber++;
            } else if (((StatsCenter) center).getCenter() instanceof Attraction) {
                attractionNumber++;
            }
        }

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
