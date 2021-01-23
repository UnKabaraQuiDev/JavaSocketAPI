package lu.poucy.jsa.utils;

public class Pair<K, V> {

	private K key;
	private V value;
	
	public Pair(K _k, V _v) {
		this.key = _k;
		this.value = _v;
	}
	
	public K getKey() {
		return key;
	}
	public V getValue() {
		return value;
	}
	
}
