import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GetLinkSummary {

    public static void main(String[] args) throws FileNotFoundException {

        Network network = NetworkUtils.readNetwork(args[0]);

        PrintWriter pw = new PrintWriter(new File(args[1]));


        pw.println("matsim_id,osm_id,length,freespeed,lanes,capacity,from_node_id,from_node_x,from_node_y,to_node_id,to_node_x,to_node_y");

        for (Link link : network.getLinks().values()){

            pw.print(link.getId());
            pw.print(",");
            pw.print(link.getAttributes().getAttribute("origid"));
            pw.print(",");
            pw.print(link.getLength());
            pw.print(",");
            pw.print(link.getFreespeed());
            pw.print(",");
            pw.print(link.getNumberOfLanes());
            pw.print(",");
            pw.print(link.getCapacity());
            pw.print(",");
            Node fromNode = link.getFromNode();
            pw.print(fromNode.getId());
            pw.print(",");
            pw.print(fromNode.getCoord().getX());
            pw.print(",");
            pw.print(fromNode.getCoord().getY());
            pw.print(",");
            Node toNode = link.getToNode();
            pw.print(toNode.getId());
            pw.print(",");
            pw.print(toNode.getCoord().getX());
            pw.print(",");
            pw.print(toNode.getCoord().getY());
            pw.println();
        }

       pw.close();

    }


}
