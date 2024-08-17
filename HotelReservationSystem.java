import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/satya_residency";
    private static final String username = "root";
    private static final String password = "nithish@1123";

    public static void main(String[] args) throws ClassNotFoundException {

        try {
            Class.forName("com.jdbc.Drivers");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println("----------------------");
                System.out.println("WELCOME TO SATYA RESIDENCY");
                Scanner sc = new Scanner(System.in);
                System.out.println("1.Reserve a room");
                System.out.println("2.view Reservation");
                System.out.println("3.Get Room Number");
                System.out.println("4.Update Reservation");
                System.out.println("5.Delete Reservation");
                System.out.println("0.Exit");
                System.out.println("Choose an object:");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(con, sc);
                        break;
                    case 2:
                        viewReservation(con);
                        break;
                    case 3:
                        getRoomNumber(con, sc);
                        break;
                    case 4:
                        updatereservation(con, sc);
                        break;
                    case 5:
                        deleteResevation(con, sc);
                        break;
                    case  0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("invailed choice .Try again!!!");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }

    }

    private static void reserveRoom(Connection con, Scanner sc) {

        try {
            System.out.print("Enter guest name:");
            String guest_name = sc.next();
            sc.nextLine();
            System.out.print("Enter room number:");
            int room_no = sc.nextInt();
            System.out.print("Enter contact number:");
            String contact = sc.next();
            String sql = "INSERT INTO reservations (gust_name, room_no, contact_no) " +
                    "VALUES ('" + guest_name + "', " + room_no + ", '" + contact + "')";
            try (Statement stmt = con.createStatement()) {
                int affectedrow = stmt.executeUpdate(sql);
                if (affectedrow > 0) {
                    System.out.println("reservation successdull !");
                } else {
                    System.out.println("reservation failes!!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection con) throws SQLException {
        String sql = "SELECT reservation_id, gust_name, room_no, contact_no, reservation_date FROM reservations";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("current Reservation");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            while (rs.next()) {
                int reservationId = rs.getInt("reservation_id");
                String gustname = rs.getString("gust_name");
                int roomno = rs.getInt("room_no");
                String contactno = rs.getString("contact_no");
                String reservationDate = rs.getTime("reservation_date").toString();
//                format and dispaly the reservation date in a table format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, gustname, roomno, contactno, reservationDate);


            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

        }

    }

    private static void getRoomNumber(Connection con, Scanner sc) {
        try {
            System.out.print("Enter resrevation ID:");
            int reservationId = sc.nextInt();
            System.out.print("enter gust name:");
            String gust_name = sc.next();
            String sql = "SELECT room_no FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND gust_name = '" + gust_name + "'";
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    int roomNumber = rs.getInt("room_no");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + gust_name + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void updatereservation(Connection con, Scanner sc) {
        try {
            System.out.print("enter reservation ID to update:");
            int reservationId = sc.nextInt();
            sc.nextLine(); //consme the new line char
            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            System.out.print("Enter new guest name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET gust_name = '" + newGuestName + "', " +
                    "room_no = " + newRoomNumber + ", " +
                    "contact_no = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;
            try (Statement statement = con.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteResevation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to delete:");
            int reservationId = sc.nextInt();
            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found for the given ID");
                return;
            }
            String sql = "DELETE FROM reservations WHERE reservation_id =" + reservationId;
            try (Statement stmt = con.createStatement()) {
                int affectedRows = stmt.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("reservation delete successfully!");
                } else {
                    System.out.println("Reservation deletion falied:");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static boolean reservationExists(Connection con,int reservationId){
        try{
            String sql="SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;
            try(Statement stmt=con.createStatement();ResultSet rs=stmt.executeQuery(sql)){
                return rs.next(); //if there's result , the reservation
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;//handle database errors as needed
        }
    }
    public static void exit() throws InterruptedException{
        System.out.print("existing system");
        int i=5;
        while (i!=0){
            System.out.print(" .");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou !!!");
    }



}

