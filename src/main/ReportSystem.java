package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;  

public class ReportSystem extends JavaPlugin implements Listener{
	Connection connection = null;
	Statement statement = null;
	ResultSet rs = null;
	public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Mikhail\\eclipse-workspace\\ReportSystem\\TheCities.db");
            System.out.println("[ReportSystem] DataBase connect successfull");
        }
        catch (Exception ex) {
            System.out.println("[ReportSystem] ERROR DataBase connection: " + ex.getClass().getName() + ": " + ex.getMessage());
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        
        try {
            statement = connection.createStatement();
        } 
        catch (SQLException ex) {
            System.out.println("[ReportSystem] ERROR create stament: " + ex.getClass().getName() + ": " + ex.getMessage());
        } 
	}
	public void onDisable() {
        try {
            connection.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println("[ReportSystem] ERROR on disable plugin" + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }
	@Override
	public boolean onCommand (CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args){
		if (commandLabel.equalsIgnoreCase("report")) {
			Player p = (Player)sender;
			if(args.length == 0) {
				p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.RED + "Для того чтобы подать жалобу на игрока используйте команду /report <никнейм игрока> <текст жалобы>");
				return true;
			}
			if(args[0].equals("patrol")) {
				try {
					rs = statement.executeQuery("SELECT from_user, suspect, reason, status FROM reports WHERE ID=" + args[1]);
					if (rs.getString(1).length() >= 10) {
						System.out.println(rs.getString(4));
						if (rs.getString(4).equals("OPENED")) {
							statement.executeUpdate("UPDATE reports SET status='PENDING' WHERE ID=" + args[1]);
							p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.DARK_AQUA + "Вы установили статус жалобы #" + args[1] + " на рассмотрении");
						}
						else {
							p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.RED + "Эта жалоба уже была закрыта или находится в состоянии рассмотрения");
						}
					}
					else {
						p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.RED + "Ошибка при выполнении команды. (Возможно такого ID жалобы не существует)");
					}
				}
				catch (SQLException ex){
					System.out.println(ex);
					p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.RED + "Ошибка при выполнении команды. (Возможно такого ID жалобы не существует)");
				}
			}
			if(args[0].equals("close")) {
				try {
					rs = statement.executeQuery("SELECT from_user, suspect, reason, status FROM reports WHERE ID=" + args[1]);
					if (rs.getString(1).length() >= 10) {
						System.out.println(rs.getString(4));
						if (rs.getString(4).equals("PENDING")) {
							statement.executeUpdate("UPDATE reports SET status='CLOSED' WHERE ID=" + args[1]);
							p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.DARK_AQUA + "Вы закрыли жалобу #" + args[1]);
						}
						else {
							p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.RED + "Эта жалоба уже была закрыта или открыта");
						}
					}
					else {
						p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.RED + "Ошибка при выполнении команды. (Возможно такого ID жалобы не существует)");
					}
				}
				catch (SQLException ex){
					System.out.println(ex);
					p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.RED + "Ошибка при выполнении команды. (Возможно такого ID жалобы не существует)");
				}
			}
			if(args[0].equals("status")) {
				try {
					rs = statement.executeQuery("SELECT from_user, suspect, reason, status, ID FROM reports WHERE ID=" + args[1]);
					if (rs.getString(1).length() >= 10) {
						UUID from_user_UUID = java.util.UUID.fromString(rs.getString(1).replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
						UUID suspect_UUID = java.util.UUID.fromString(rs.getString(2).replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
						String reason = rs.getString(3);
						String status = rs.getString(4);
						int ID = rs.getInt(5);
						p.sendMessage(ChatColor.YELLOW + "ID:" + ID);
						p.sendMessage(ChatColor.YELLOW + "Жалоба от игрока: " + Bukkit.getPlayer(from_user_UUID).getName());
						p.sendMessage(ChatColor.YELLOW + "Подозреваемый: " + ChatColor.RED +ChatColor.UNDERLINE + Bukkit.getPlayer(suspect_UUID).getName());
						p.sendMessage(ChatColor.YELLOW + "Текст жалобы: " + reason);
						p.sendMessage(ChatColor.YELLOW + "Состоянии жалобы: " + ChatColor.BOLD + status);
						p.sendMessage(ChatColor.AQUA + " ");
					}
					else {
						p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.RED + "Ошибка при выполнении команды. (Возможно такого ID жалобы не существует)");
					}
				}
				catch (SQLException ex){
					System.out.println(ex);
					p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.RED + "Ошибка при выполнении команды. (Возможно такого ID жалобы не существует)");
				}
			}
			try {
				if(args.length >= 2 & !args[0].equals("patrol") & !args[0].equals("close") & !args[0].equals("status")){
					String suspect = Bukkit.getPlayer(args[0]).getUniqueId().toString().replaceAll("-", "");
					String player_uuid = p.getUniqueId().toString().replaceAll("-", "");
					String reason = "";
					int counter = 0;
					for (String i:args) {
						if (counter == 0) {
							counter += 1;
							continue;}
						else {
							reason += ' ' + i;
						}
					}
					statement.executeUpdate("INSERT INTO reports VALUES('" + player_uuid + "', '" + suspect +"', NULL, '" + reason +"', 'OPENED', NULL)");
					p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.GREEN + "Спасибо за жалобу. В ближайшее время администрация рассмотрит ваше обращение.");
					for (Player admin : Bukkit.getOnlinePlayers()) {
					    if(admin.isOp() == true) {
					    	admin.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.GREEN + "Поступила новая жалоба. Напишите /reports чтобы посмотреть.");
					    }
					}
				}
				else {
					return true;
				}
			}
			catch (SQLException ex){
				System.out.println(ex);
				p.sendMessage(ChatColor.YELLOW + "[ReportSystem] " + ChatColor.RED + "Для того чтобы подать жалобу на игрока используйте команду /report <никнейм игрока> <текст жалобы>");
				return true;
			}
		}
		if (commandLabel.equalsIgnoreCase("reports")) {
			Player p = (Player)sender;
			try {
				rs = statement.executeQuery("SELECT from_user, suspect, reason, ID FROM reports WHERE status='OPENED'");
				int counter = 0;
				while(rs.next()) {
					counter += 1;
					UUID from_user_UUID = java.util.UUID.fromString(rs.getString(1).replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
					UUID suspect_UUID = java.util.UUID.fromString(rs.getString(2).replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
					String reason = rs.getString(3);
					int ID = rs.getInt(4);
					p.sendMessage(ChatColor.YELLOW + "ID:" + ID);
					p.sendMessage(ChatColor.YELLOW + "Жалоба от игрока: " + Bukkit.getPlayer(from_user_UUID).getName());
					p.sendMessage(ChatColor.YELLOW + "Подозреваемый: " + ChatColor.RED + ChatColor.UNDERLINE + Bukkit.getPlayer(suspect_UUID).getName());
					p.sendMessage(ChatColor.YELLOW + "Текст жалобы: " + reason);
					p.sendMessage(ChatColor.AQUA + " ");
				}
				p.sendMessage(ChatColor.GREEN + "Всего открытых жалоб: " + counter);
				p.sendMessage(ChatColor.RED + "Для того чтобы рассмотреть жалобу напишите /report patrol <ID>");
			}
			catch (SQLException ex) {
	            System.out.println("[ReportSystem] ERROR request: " + ex.getClass().getName() + ": " + ex.getMessage());
	        }
			return true;
		}
		
		
		return true;}
}