
public class Driver {
	public static void main(String [] args){
		Count count = new Count(args[0]);
		count.run();
		System.out.println("total number of Ethernet (IP + ARP) packets = " + count.getEthernet());
		System.out.println("total number of IP packets = " + count.getIP());
		System.out.println("total number of ARP packets = " + count.getARP());
		System.out.println("total number of ICMP packets = " + count.getICMP());
		System.out.println("total number of TCP packets = " + count.getTCP());
		System.out.println("total number of UDP packets = " + count.getUDP());
		System.out.println("total number of Ping packets = " + count.getPing());
		System.out.println("total number of DHCP packets = " + count.getDHCP());
		System.out.println("total number of DNS packets = " + count.getDNS());
	}
}
