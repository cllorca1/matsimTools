import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

public class RunReduceSpeedLimitOverall {

    public static void main(String[] args) {

        Network network = NetworkUtils.readNetwork(args[0]);

        for (Link link : network.getLinks().values()){

            if (link.getFreespeed() > 70 / 3.6 && !link.getAllowedModes().contains("pt")){

                link.setFreespeed(70/3.6);
            }
        }

        NetworkUtils.writeNetwork(network, args[1]);

    }


}
