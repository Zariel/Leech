package fein.leech.usenet;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class UsenetStream {
	private Socket socket;
	private OutputStreamWriter out;
	private InputStreamReader in;
	private String current_group;
	public static String CRLF = new String(new char[]{ 0xd, 0xa });
	
	public UsenetStream(String host, int port) throws IOException, Exception {
		socket = new Socket(host, port);
		out = new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream()), "US-ASCII");
		//out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("C:\\out.txt")), "US-ASCII");
		in = new InputStreamReader(socket.getInputStream(), "US-ASCII");
		
		String buf = read(128);
		
		if(parse_code(buf) != 200) {
			throw new Exception();
		}
		
		System.out.println(buf);		
	}
	
	public void close() throws IOException {
		out.close();
		in.close();
		socket.close();
	}
	
	protected void finalize() throws Throwable {
		try {
			close();
		} finally {
			super.finalize();
		}
	}
	
	public int send(String data) {
		System.out.println("> " + data);
		
		try {
			out.write(data + CRLF, 0, data.length() + CRLF.length());
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
		return 0;
	}
	
	public String read(int size) {
		char[] buf = new char[size];
		int len = 0;
		
		try {
			len = in.read(buf, 0, size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(len < 1) {
			return null;
		}
		
		StringBuilder retB = new StringBuilder();
		
		int i = 2;
		int offset = 0;
		while(i < len) {
			if(buf[i - 1] == 0xd && buf[i] == 0xa) {
				retB.append(buf, offset, i - 1 - offset);
				retB.append('\n');
				offset = i + 1;
				
				if(i < len + 3) {
					if(buf[i + 1] == '.' && buf[i + 2] == 0xd && buf[i + 3] == 0xa) {
						break;
					}
				}
			}
			
			i++;
		}
		
		retB.deleteCharAt(retB.length() - 1);
		
		/*
		char ret[] = new char[i + 1];
		while(i > -1) {
			ret[i] = buf[i];
			i--;
		}
		*/
		
		//TODO: if we dont find CRLF then the string hasnt finished sending so we must read more.

		return retB.toString();
	}
	
	public char[] read() {
		int err = 0;
		char[] buf = new char[1024];
		
		try {
			err = in.read(buf, 0, 1024);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return buf;
	}
	
	/* TODO: Use byte[], which should be faster ? */
	
	public int parse_code(String data) {
		String code = data.substring(0, 3);
		
		// Dirty way of doing it
		return Integer.parseInt(code);
	}

	public int quit() {
		send("QUIT");
		int ret = parse_code(read(128));
		return ret;
	}

	public int article(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int group(String group) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int header(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int body(int id) {
		// TODO Auto-generated method stub
		return 0;
	}


}
