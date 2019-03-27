package uk.ac.ncl.DnsZ.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Club {
	String name;
	int goalScored;
	int goalConced;
	int goalDifference;
	int pointTally;
	List<Player> squad;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGoalScored() {
		return goalScored;
	}

	public void addToGoalScored(int goalScored) {
		this.goalScored = this.goalScored + goalScored;
	}

	public int getGoalConced() {
		return goalConced;
	}

	public void addToGoalConced(int goalConced) {
		this.goalConced = this.goalConced + goalConced;
	}

	public int getGoalDifference() {
		this.goalDifference = this.goalScored - this.goalConced;
		return goalDifference;
	}

	public int getPointTally() {
		return pointTally;
	}

	public void setPointTally(int pointTally) {
		this.pointTally = pointTally;
	}

	public List<Player> getSquad() {
		return squad;
	}

	public void addToSquad(Player newPlayer) {
		this.squad.add(newPlayer);
	}

	public void speak() {
		System.out.println("this is club");
	};

	public boolean isPlayerInSquard(String playerName) {
		for (Player p : this.squad) {
			if (p.getName().equals(playerName))
				return true;
		}
		return false;
	}

	public double findAverageAge() {
		int totalAge = 0;
		for (Player p : this.squad) {
			totalAge = totalAge + getAge(p.getBirthday());
		}
		return totalAge / squad.size();
	}

	public double findAverageHeight() {
		double totalHeight = 0;
		for (Player p : this.squad) {
			totalHeight = totalHeight + p.getHeight();
		}
		return totalHeight / squad.size();
	}

	public static int getAge(Date dateOfBirth) {
		int age = 0;
		Calendar born = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		if (dateOfBirth != null) {
			now.setTime(new Date());
			born.setTime(dateOfBirth);
			if (born.after(now)) {
				throw new IllegalArgumentException("illegal name founded");
			}
			age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
			int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
			int bornDayOfYear = born.get(Calendar.DAY_OF_YEAR);
			System.out.println("nowDayOfYear:" + nowDayOfYear + " bornDayOfYear:" + bornDayOfYear);
			if (nowDayOfYear < bornDayOfYear) {
				age -= 1;
			}
		}
		return age;
	}

}

public class UkClub extends Club {
	ukClub(){
		super.name = "this is UK Club";
	}

	@Override
	public void speak() {
		System.out.println(super.name);
	}
}
