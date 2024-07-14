package net.justonedev.mc.warpbooks;

import java.util.Objects;

public class Triple<TK, TV, TU> {
	
	public TK Item1;
	public TV Item2;
	public TU Item3;
	
	public Triple(TK item1, TV item2, TU item3)
	{
		this.Item1 = item1;
		this.Item2 = item2;
		this.Item3 = item3;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Triple<?, ?, ?> that = (Triple<?, ?, ?>) o;
		return Objects.equals(Item1, that.Item1) && Objects.equals(Item2, that.Item2) && Objects.equals(Item3, that.Item3);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(Item1, Item2, Item3);
	}
}