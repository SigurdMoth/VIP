package dk.vip.session.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dk.vip.session.domain.network.models.VipNetwork;
import dk.vip.session.domain.network.models.VipNode;
import dk.vip.session.domain.transmit.ITransmissionHandler;
import dk.vip.session.presentation.IWrapHandler;
import dk.vip.wrap.MetaBundle;
import dk.vip.wrap.MetaCollection;
import dk.vip.wrap.Wrap;

@Component
public class WrapHandlerImp implements IWrapHandler {
    Logger logger = Logger.getLogger(WrapHandlerImp.class.getName());

    @Autowired
    ITransmissionHandler transmissionHandler;

    public String handleClientWrap(String requestBody) {
        // Unwrap clientWrap
        Gson gson = new Gson();
        Wrap clientWrap = null;
        try {
            clientWrap = gson.fromJson(requestBody, Wrap.class);
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Failed to unwrap Json wrapper", e);
        }
        // unwrap metabundle
        if (clientWrap != null) {
            MetaCollection metaCollection = clientWrap.getMetaCollection();
            MetaBundle networkBundle = metaCollection.getBundles().get("networkbundle");
            String network = (String) networkBundle.getProperties().get("network");
            String node = (String) networkBundle.getProperties().get("node");
            logger.log(Level.INFO, networkBundle.getProperties().entrySet().toString());

            // map network / load network
            VipNetwork vipNetwork1 = new VipNetwork(0);
            vipNetwork1.addNode(new VipNode(0, "198.168.1.2", "25::10", Arrays.asList(80, 8080, 443)));
            vipNetwork1.addNode(new VipNode(1, "198.168.1.3", "26::11", Arrays.asList(200, 614, 20)));
            VipNetwork vipNetwork2 = new VipNetwork(1);
            vipNetwork2.addNode(new VipNode(0, "198.168.1.4", "15::2", Arrays.asList(100, 314, 10)));
            vipNetwork2.addNode(new VipNode(1, "198.168.1.5", "16::4", Arrays.asList(200, 614, 20)));
            VipNetwork vipNetwork3 = new VipNetwork(5);
            vipNetwork3.addNode(new VipNode(0, "198.168.1.6", "36::3", Arrays.asList(100, 314, 10)));
            vipNetwork3.addNode(new VipNode(1, "198.168.1.7", "46::41", Arrays.asList(200, 614, 20)));
            VipNetwork vipNetwork4 = new VipNetwork(9);
            vipNetwork4.addNode(new VipNode(0, "198.168.1.8", "66::15", Arrays.asList(100, 314, 10)));
            vipNetwork4.addNode(new VipNode(1, "198.168.1.9", "77::16", Arrays.asList(200, 614, 20)));

            List<VipNetwork> networks = new ArrayList<>();
            networks.add(vipNetwork1);
            networks.add(vipNetwork2);
            networks.add(vipNetwork3);
            networks.add(vipNetwork4);

            VipNetwork foundNetwork = networks.stream().filter(n -> Integer.valueOf(network).equals(n.getId()))
                    .findAny().orElse(null);

            if (foundNetwork != null) {

                VipNode foundNode = foundNetwork.getNode(Integer.valueOf(node));
                if (foundNode != null) {
                    networkBundle.put("ip", foundNode.getIp());
                    networkBundle.put("mac", foundNode.getMac());
                    networkBundle.put("ports", foundNode.getPorts());
                    logger.log(Level.INFO, "Found node: " + foundNode);
                }
            }
        }
        // Inform VIP internal network of IP...etc.//

        // wrap && Redirect to Protocol broker
        return transmissionHandler.handleTransmission(clientWrap, "http://localhost:8081/protocolbroker/handleRoute");

        /**
         * List<Network>
         * 
         * 9 : Network { nodes : [ { "node" : "0", "network" : "9", "IP" :
         * "192.168.1.10", "MAC": "ABC::FFFF", ports : [ 443, 80, 8080 ] },{ "node" :
         * "1", "network" : "9", "IP" : "192.168.1.10", "MAC": "ABC::FFFF", ports : [
         * 443, 80, 8080 ] } ] }
         * 
         * 
         */
    }
}