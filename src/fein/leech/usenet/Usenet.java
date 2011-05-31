package fein.leech.usenet;

public interface Usenet {
	public int quit();
	public int article(int id);
	public int group(String group);
	public int header(int id);
	public int body(int id);
}
