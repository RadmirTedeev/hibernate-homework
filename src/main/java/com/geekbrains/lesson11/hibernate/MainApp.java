package com.geekbrains.lesson11.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.Query;
import java.util.List;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(Order.class)
                .buildSessionFactory();

        Session session = null;

        try {
            session = factory.getCurrentSession();
            consoleApp(session);
        } finally {
            factory.close();
            assert session != null;
            session.close();
        }
    }

    public static void consoleApp(Session session) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите операцию: ");
        String operation = scanner.nextLine();

        switch (operation) {
            case "buy":
                buyProduct(session);
                break;
            case "show":
                showProductsByPerson(session);
                break;
            case "find":
                findPersonsByProductTitle(session);
                break;
            case "removeC":
                removeCustomer(session);
                break;
            case "removeP":
                removeProduct(session);
                break;
            default:
                System.out.println("Операция отменена");
                break;
        }
    }

    public static Customer getCustomerByName(Session session, String name) {
        String queryString = "from Customer where name = :value";
        Query queryObject = session.createQuery(queryString);
        queryObject.setParameter("value", name);

        return (Customer) queryObject.getSingleResult();
    }

    public static Product getProductByTitle(Session session, String title) {
        String queryString = "from Product where title = :value";
        Query queryObject = session.createQuery(queryString);
        queryObject.setParameter("value", title);

        return (Product) queryObject.getSingleResult();
    }

    public static void showProductsByPerson(Session session) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя клиента: ");
        String name = scanner.nextLine();

        session.beginTransaction();
        Customer customer = getCustomerByName(session, name);
        List<Order> orders = customer.getOrders();
        System.out.println("Покупатель '" + customer.getName() + "' купил:");

        for (Order order : orders) {
            System.out.println("===> " + order.getProduct().getTitle() + " по цене: " + order.getPrice());
        }

        session.getTransaction().commit();
    }

    public static void findPersonsByProductTitle(Session session) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите название товара: ");
        String title = scanner.nextLine();

        session.beginTransaction();
        Product product = getProductByTitle(session, title);
        List<Order> orders = product.getOrders();
        System.out.println("Товар '" + product.getTitle() + "' купили:");

        for (Order order : orders) {
            System.out.println("===> " + order.getCustomer().getName());
        }

        session.getTransaction().commit();
    }

    public static void removeCustomer(Session session) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя покупателя для удаления: ");
        String name = scanner.nextLine();

        session.beginTransaction();
        Customer customer = getCustomerByName(session, name);
        session.delete(customer);
        System.out.println(customer + " был успешно удален");
        session.getTransaction().commit();
    }

    public static void removeProduct(Session session) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите название товара для удаления: ");
        String title = scanner.nextLine();

        session.beginTransaction();
        Product product = getProductByTitle(session, title);
        session.delete(product);
        System.out.println(product + " был успешно удален");
        session.getTransaction().commit();
    }

    public static void buyProduct(Session session) {
        Scanner scannerCustomer = new Scanner(System.in);
        Scanner scannerProduct = new Scanner(System.in);

        System.out.println("Введите имя покупателя: ");
        String customerName = scannerCustomer.nextLine();
        System.out.println("Введите название товара: ");
        String productTitle = scannerProduct.nextLine();

        session.beginTransaction();
        Customer customer = getCustomerByName(session, customerName);
        Product product = getProductByTitle(session, productTitle);

        OrderId orderId = new OrderId();
        orderId.setCustomerId(customer.getId());
        orderId.setProductId(product.getId());

        Order order = new Order();
        order.setId(orderId);
        order.setPrice(product.getPrice());

        session.save(order);
        System.out.println("Покупатель " + customer.getName() + " купил " + product.getTitle() + " по цене: " + product.getPrice());
        session.getTransaction().commit();
    }
}
