/**
 * 
 */
package test.net.saff.floyd;

import com.domainlanguage.money.Money;

public class FloydMoney implements Comparable<FloydMoney> {

	private final Money money;

	public FloydMoney(Money dollars) {
		this.money = dollars;
	}

	public static FloydMoney parseDollars(String string) {
		return new FloydMoney(Money.dollars(Double.valueOf(string)));
	}

	@Override
	public String toString() {
		return money.toString().replace(" ", "");
	}

	public static FloydMoney dollars(double d) {
		return new FloydMoney(Money.dollars(d));
	}

	public FloydMoney minus(FloydMoney subtractant) {
		return new FloydMoney(money.minus(subtractant.money));
	}

	public FloydMoney plus(FloydMoney amount) {
		return new FloydMoney(money.plus(amount.money));
	}

	@Override
	public boolean equals(Object obj) {
		return ((FloydMoney) obj).money.equals(money);
	}

	public boolean lessThan(FloydMoney other) {
		return money.isLessThan(other.money);
	}

	public boolean greaterThan(FloydMoney other) {
		return money.isGreaterThan(other.money);
	}

	public int compareTo(FloydMoney o) {
		return money.compareTo(o.money);
	}
}