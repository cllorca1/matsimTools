import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GetAttributeFromNetwork {

    public static void main(String[] args) throws FileNotFoundException {

        Network network = NetworkUtils.readNetwork(args[0]);

        PrintWriter pw = new PrintWriter(new File(args[1]));

        for (Link link : network.getLinks().values()){

            String type = (String) link.getAttributes().getAttribute("admin_type");
            pw.println(link.getId().toString() + "," + type);
        }

       pw.close();

    }


}
