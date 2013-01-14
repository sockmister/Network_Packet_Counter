import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class Count {
	private int ethernet;
	private int ip;
	private int arp;
	private int icmp;
	private int tcp;
	private int udp;
	private int ping;
	private int dhcp;
	private int dns;
	
	private BufferedReader br;
	private Vector<Vector<String>> frames;
	
	public Count(String filename){
		ethernet = ip = arp = icmp = tcp = udp = ping = dhcp = dns = 0;
		Path path = Paths.get(filename);
		frames = new Vector<Vector<String>>();
		try {
			br = Files.newBufferedReader(path, Charset.forName("US-ASCII"));
			String line;
			Vector<String> frame = new Vector<String>();
			while((line = br.readLine()) != null){
				if(line.isEmpty()){
					if(!frame.isEmpty()){
						frames.add(frame);
						frame = new Vector<String>();
					}
				}
				else{
					frame.add(line);
				}
			}
			frames.add(frame);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		for(int i = 0; i < frames.size(); i++){
			if(frames.get(i).get(0).contains("Reassembled") || 
					frames.get(i).get(0).contains("Uncompressed")){
				i++;
			}
			else if(!frames.get(i).get(0).startsWith("0000")){
				//ignore
			}
			else{
				Vector<String> hexas = new Vector<String>();
				convertFrameToHexas(frames.get(i), hexas);
				ethernet(hexas, 0);
			}
		}
	}
	
	private void ethernet(Vector<String> hexas, int start){
		//check if ethernet, otherwise ignore
		//check for ether type
		String etherType = hexas.get(12);
		etherType = etherType.concat(hexas.get(13));
		if(etherType.equals("0800")){	//IP type
			ethernet++;
			ip(hexas, 14);
		}
		else if(etherType.equals("0806")){
			ethernet++;
			arp(hexas, 14);
		}
		else{
			/*
			for(String line : hexas)
				System.out.print(line + " ");
			System.out.println();
			*/
		}
	}
	
	private void ip(Vector<String> hexas, int start){
		ip++;
		String protocol = hexas.get(start + 9);
		String headerLengthStr = String.valueOf(hexas.get(start).charAt(1));
		int headerLength = Integer.parseInt(headerLengthStr, 16) * 4;
		
		switch(protocol){
			case "01":
				icmp(hexas, start+headerLength);
				break;
			case "06":
				tcp(hexas, start+headerLength);
				break;
			case "11":
				udp(hexas, start+headerLength);
				break;
				
		}
	}
	
	private void icmp(Vector<String> hexas, int start){
		icmp++;
		String type = hexas.get(start);
		if(type.equals("00") || type.equals("08"))
			ping++;
	}
	
	private void tcp(Vector<String> hexas, int start){
		tcp++;
		String srcPort = hexas.get(start);
		srcPort = srcPort.concat(hexas.get(start+1));
		String destPort = hexas.get(start + 2);
		destPort = destPort.concat(hexas.get(start+3));
		
		if(srcPort.equals("0035") || destPort.equals("0035")){
			dns++;
		}  
	}
	
	private void udp(Vector<String> hexas, int start){
		udp++;
		String srcPort = hexas.get(start);
		srcPort = srcPort.concat(hexas.get(start+1));
		String destPort = hexas.get(start + 2);
		destPort = destPort.concat(hexas.get(start+3));
		
		if(srcPort.equals("0044") || srcPort.equals("0043"))
			dhcp++;
		else if(srcPort.equals("0035") || destPort.equals("0035"))
			dns++;
			
	}
	
	private void arp(Vector<String> hexas, int start){
		//probably don't need it
		arp++;
	}
	
	private void convertFrameToHexas(Vector<String> frame, Vector<String> hexas){
		for(String line : frame){
			String [] splitLine = line.split("\\s+");
			for(int i = 1; i <= 16 && i < splitLine.length-1; i++){
				if(isHexa(splitLine[i]))
					hexas.add(splitLine[i]);
			}
		}
	}
	
	private boolean isHexa(String str){
		if(str.length() == 2){
			return isHexStringChar(str.charAt(0)) && isHexStringChar(str.charAt(1));
		}
		else
			return false;
	}
	
	private void printAll(){
		for(Vector<String>frame : frames){
			for(String line : frame)
				System.out.println(line);
		}
	}
	
	private boolean isHexStringChar(char c) {
		return (Character.isDigit(c) ||
		Character.isWhitespace(c) || 
		(("0123456789abcdefABCDEF".indexOf(c)) >= 0));
	}
	
	public int getEthernet(){
		return ethernet;
	}
	
	public int getIP(){
		return ip;
	}
	
	public int getARP(){
		return arp;
	}
	
	public int getICMP(){
		return icmp;
	}
	
	public int getTCP(){
		return tcp;
	}
	
	public int getUDP(){
		return udp;
	}
	
	public int getPing(){
		return ping;
	}
	
	public int getDHCP(){
		return dhcp;
	}
	
	public int getDNS(){
		return dns;
	}
}
