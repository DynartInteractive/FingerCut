package info.dynart.fingercut;

public interface ILeaderboard {
	
	public void initLeaderboard();
	public void showLeaderboard();
	public void submitScore(int score);
	public void showAchievements();
	public void unlockAchievement(FingerCut.Achi achi);
	
}
