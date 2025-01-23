package gijin.chatbot.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtils
{

    public static String getLocalIpAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println(localHost);
            return localHost.getHostAddress(); // 로컬 IP 주소 반환
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unable to get IP address";
        }
    }
}
