package com.pluralsight;

import com.pluralsight.dao.ShippersDao;
import org.apache.commons.dbcp2.BasicDataSource;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java com.pluralsight.App <username> <password>");
            System.exit(1);
        }

        String username = args[0];
        String password = args[1];

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/northwind");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        ShippersDao shipperDao = new ShippersDao(dataSource);
        Scanner input = new Scanner(System.in);

        int option;
        do {
            System.out.println("\n--- Shipper Management Menu ---");
            System.out.println("1. Add a new shipper");
            System.out.println("2. Update shipper phone number");
            System.out.println("3. Delete a shipper");
            System.out.println("4. View all shippers");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            option = input.nextInt();
            input.nextLine(); // clear newline

            switch (option) {
                case 1 -> {
                    System.out.print("Enter a new shipper name and phone (separated by a space): ");
                    String[] shipperData = input.nextLine().trim().split("\\s+", 2);
                    if (shipperData.length < 2) {
                        System.out.println("Invalid input. Please enter both name and phone.");
                        break;
                    }
                    int newId = shipperDao.insertShipper(shipperData[0], shipperData[1]);
                    System.out.println("Shipper ID: " + newId + " successfully inserted!");
                    shipperDao.displayShippers(); // ✅ show updated list
                }
                case 2 -> {
                    System.out.print("Enter an existing ID and new phone number (separated by a space): ");
                    String[] updateData = input.nextLine().trim().split("\\s+", 2);
                    if (updateData.length < 2) {
                        System.out.println("Invalid input. Please enter both ID and phone.");
                        break;
                    }
                    int updateId = Integer.parseInt(updateData[0]);
                    String newPhone = updateData[1];
                    shipperDao.updateShipperPhone(updateId, newPhone);
                    System.out.println("Shipper with ID: " + updateId + " phone successfully updated!");
                    shipperDao.displayShippers(); // ✅ show updated list
                }
                case 3 -> {
                    System.out.print("Enter a shipper ID to delete (1–3 not allowed): ");
                    int deleteId = input.nextInt();
                    input.nextLine(); // clear newline
                    if (deleteId <= 3) {
                        System.out.println("Not allowed to delete ID 1–3!");
                    } else {
                        shipperDao.deleteShipper(deleteId);
                        System.out.println("Shipper with ID: " + deleteId + " successfully deleted!");
                        shipperDao.displayShippers();
                    }
                }
                case 4 -> shipperDao.displayShippers();
                case 0 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid option. Try again.");
            }

        } while (option != 0);

        input.close();
    }
}
