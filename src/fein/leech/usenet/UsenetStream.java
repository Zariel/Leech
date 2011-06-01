package fein.leech.usenet;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class UsenetStream {
	private Socket socket;
	private OutputStreamWriter out;
	private InputStream in;
	private String current_group;
	public static String CRLF = new String(new char[]{ 0xd, 0xa });

	public UsenetStream(String host, int port) throws IOException, Exception {
		socket = new Socket(host, port);
		out = new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream()), "US-ASCII");
		//out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("C:\\out.txt")), "US-ASCII");
		in = socket.getInputStream();

		byte[] buf = read();

		/*
		if(parse_code(buf) != 200) {
			throw new Exception();
		}
		*/

		System.out.println(new String(buf, "US-ASCII"));		
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
	
	public byte[] read() throws IOException {
		/* The max a single response can be is 512 bytes, but multi lines can go over this */
		int size = 512;
		int len = 0;
				
		byte[] recv = new byte[size];

		try {
			len = in.read(recv, 0, size);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		int available = in.available();
		
		/* Multi line */
		if(available > 0) {			
			byte[] new_buffer = new byte[len + available];
			System.arraycopy(recv, 0, new_buffer, 0, len);
			
			try {
				in.read(new_buffer, len, available);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			recv = new_buffer;
			len += available;
		}
		
		byte[] buffer = new byte[len];
		
		int i = 0;
		int j = 0;
		
		boolean multi = false;
		
		while(i < len - 1) {
			if(recv[i] == 0xd && recv[i + 1] == 0xa) {
				buffer[j++] = 0xa;
				
				if(i < len - 4 && recv[i + 2] == 0x2e && recv[i + 3] == 0xd && recv[i + 4] == 0xa) {
					multi = true;
					break;
				}
				
				i += 2;
				
			} else {
				buffer[j++] = recv[i++];
			}
		}
		
		byte[] _buf = new byte[j];
		
		System.arraycopy(buffer, 0, _buf, 0, j);
			
		return _buf;
	}
/*
	public byte[] read(int size) {
		byte[] buf = new byte[size];
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
		
		byte[] buffer = new byte[len + 1];

		int j = 0;
		int i = 0;
		
		while(i + 1 < len) {
			if(buf[i] == 0xd && buf[i + 1] == 0xa) {
				i += 2;
				buffer[j++] = 0xa;
			} else {
				buffer[j++] = buf[i++];
			}
		}
		
		buffer[j + 1] = 0x0;
		
		return buffer;
	}
/*
	/* TODO: Use byte[], which should be faster ? */
/*
	public int parse_code(byte[] bs) {
		char[] array = new char[3];
		for(int i = 0; i < 3; i++) {
			array[i] = bs[i];
		}

		// Dirty way of doing it
		return Integer.parseInt(new String(array));
	}
	*/

	public int quit() {
		send("QUIT");
		//int ret = parse_code(read(128));
		return 0;
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
