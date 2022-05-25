import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.server.UID;
import java.security.spec.NamedParameterSpec;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.Color;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import net.proteanit.sql.DbUtils;

public class main {


    public static class ex {
        public static int days = 0;
    }

    public static void main(String[] args) {

        login();
        //create();
    }

    public static void login() {

        JFrame f = new JFrame("Login");
        JLabel l1, l2;
        l1 = new JLabel("Username");
        l1.setBounds(30, 15, 100, 30);

        l2 = new JLabel("Password");
        l2.setBounds(30, 50, 100, 30);

        JTextField F_user = new JTextField();
        F_user.setBounds(110, 15, 200, 30);

        JPasswordField F_pass = new JPasswordField();
        F_pass.setBounds(110, 50, 200, 30);

        JButton close = new JButton("Exit");
        close.setBounds(230, 90, 80, 25);
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.exit(0);
            }
        });

        JButton login = new JButton("Login");
        login.setBounds(120, 90, 80, 25);
        login.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String username = F_user.getText();
                String password = F_pass.getText();

                if (username.equals(""))
                {
                    JOptionPane.showMessageDialog(null, "Tolong masukkan username anda.");
                } else if (password.equals(""))
                {
                    JOptionPane.showMessageDialog(null, "Tolong masukan password anda.");
                } else {
                    //System.out.println("Login connect");
                    Connection connection = connect();
                    try {
                        Statement stmt =
                                connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY);
                        stmt.executeUpdate("USE LIBRARY");
                        String st = ("SELECT * FROM USERS WHERE USERNAME='" + username + "' AND PASSWORD='" + password + "'");
                        ResultSet rs = stmt.executeQuery(st);
                        if (rs.next() == false) {
                            System.out.print("No user");
                            JOptionPane.showMessageDialog(null, "Username/Password anda salah.");

                        } else {
                            f.dispose();
                            rs.beforeFirst();
                            while (rs.next()) {
                                String admin = rs.getString("ADMIN");
                                //System.out.println(admin);
                                String UID = rs.getString("UID");
                                if (admin.equals("1")) {
                                    admin_menu();
                                } else {
                                    user_menu(UID);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


        f.add(F_pass);
        f.add(close);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(login);
        f.add(F_user);
        f.add(l1);
        f.add(l2);
        f.setResizable(false);
        f.setSize(400, 180);
        f.setLayout(null);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setResizable(false);

    }

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //System.out.println("Loaded driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "Karasuma1");
            //System.out.println("Connected to MySQL");
            return con;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void create() {
        try {
            Connection connection = connect();
            ResultSet resultSet = connection.getMetaData().getCatalogs();

            while (resultSet.next()) {

                String databaseName = resultSet.getString(1);
                if (databaseName.equals("library")) {
                    Statement stmt = connection.createStatement();
                    String sql = "DROP DATABASE library";
                    stmt.executeUpdate(sql);
                }
            }
            Statement stmt = connection.createStatement();

            String sql = "CREATE DATABASE LIBRARY";
            stmt.executeUpdate(sql);
            stmt.executeUpdate("USE LIBRARY");
            String sql1 = "CREATE TABLE USERS(UID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, USERNAME VARCHAR(30), PASSWORD VARCHAR(30), ADMIN BOOLEAN)";
            stmt.executeUpdate(sql1);
            stmt.executeUpdate("INSERT INTO USERS(USERNAME, PASSWORD, ADMIN) VALUES('admin','admin',TRUE)");
            stmt.executeUpdate("CREATE TABLE BOOKS(BID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(50), GENRE VARCHAR(20), PRICE INT)");
            stmt.executeUpdate("CREATE TABLE ISSUED(IID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, UID INT, BID INT, ISSUED_DATE VARCHAR(20), RETURN_DATE VARCHAR(20), PERIOD INT, FINE INT)");
            stmt.executeUpdate("INSERT INTO BOOKS(NAME, GENRE, PRICE) VALUES ('ONE PIECE 100', 'Adventure', 450),  ('SLAM DUNK 1', 'Sports', 430), ('Shikimori is Not Just a Cutie! 1','Romantic Comedy', 550), ('The Remnants of Agamemnon', 'Mystery', 780), ('How To Handle Anxiety','How-To', 1050)");

            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void user_menu(String UID) {

        JFrame f = new JFrame("User Menu");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton view = new JButton("Cek Buku");
        view.setBounds(20, 20, 120, 25);
        view.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {

                                       JFrame f = new JFrame("List Buku");


                                       Connection connection = connect();
                                       String sql = "select * from BOOKS";
                                       try {
                                           Statement stmt = connection.createStatement();
                                           stmt.executeUpdate("USE LIBRARY");
                                           stmt = connection.createStatement();
                                           ResultSet rs = stmt.executeQuery(sql);
                                           JTable book_list = new JTable();
                                           book_list.setModel(DbUtils.resultSetToTableModel(rs));

                                           JScrollPane scrollPane = new JScrollPane(book_list);

                                           f.add(scrollPane);
                                           f.setSize(800, 400);
                                           f.setVisible(true);
                                           f.setLocationRelativeTo(null);
                                       } catch (SQLException e1) {
                                           // TODO Auto-generated catch block
                                           JOptionPane.showMessageDialog(null, e1);
                                       }

                                   }
                               }
        );

        JButton close = new JButton("Exit");
        close.setBounds(150, 60, 120, 25);
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.exit(0);
            }
        });

        JButton my_book = new JButton("Buku Saya");
        my_book.setBounds(150, 20, 120, 25);
        my_book.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e) {


                                          JFrame f = new JFrame("Buku Saya");
                                          int UID_int = Integer.parseInt(UID);

                                          //.iid,issued.uid,issued.bid,issued.issued_date,issued.return_date,issued,
                                          Connection connection = connect();
                                          String sql = "select distinct issued.*,books.name,books.genre,books.price from issued,books " + "where ((issued.uid=" + UID_int + ") and (books.bid in (select bid from issued where issued.uid=" + UID_int + "))) group by iid";
                                          String sql1 = "select bid from issued where uid=" + UID_int;
                                          try {
                                              Statement stmt = connection.createStatement();
                                              stmt.executeUpdate("USE LIBRARY");
                                              stmt = connection.createStatement();
                                              ArrayList books_list = new ArrayList();


                                              ResultSet rs = stmt.executeQuery(sql);
                                              JTable book_list = new JTable();
                                              book_list.setModel(DbUtils.resultSetToTableModel(rs));
                                              JScrollPane scrollPane = new JScrollPane(book_list);

                                              f.add(scrollPane);
                                              f.setSize(800, 400);
                                              f.setVisible(true);
                                              f.setLocationRelativeTo(null);
                                          } catch (SQLException e1) {
                                              // TODO Auto-generated catch block
                                              JOptionPane.showMessageDialog(null, e1);
                                          }

                                      }
                                  }
        );


        f.add(my_book);
        f.add(close);
        f.add(view);
        f.setSize(300, 150);
        f.setLayout(null);
        f.setVisible(true);
        f.setResizable(false);
        f.setLocationRelativeTo(null);
    }

    public static void admin_menu() {

        JFrame f = new JFrame("Admin Menu");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton close = new JButton("Exit");
        close.setBounds(450, 100, 120, 25);
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.exit(0);
            }
        });

        JButton create = new JButton("Reset");
        create.setBounds(450, 60, 120, 25);
        create.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                create();
                JOptionPane.showMessageDialog(null, "Database berhasil di-reset!");

            }
        });


        JButton view = new JButton("Cek Buku");
        view.setBounds(20, 20, 120, 25);
        view.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {

                                       JFrame f = new JFrame("List Buku");


                                       Connection connection = connect();
                                       String sql = "select * from BOOKS";
                                       try {
                                           Statement stmt = connection.createStatement();
                                           stmt.executeUpdate("USE LIBRARY");
                                           stmt = connection.createStatement();
                                           ResultSet rs = stmt.executeQuery(sql);
                                           JTable book_list = new JTable();
                                           book_list.setModel(DbUtils.resultSetToTableModel(rs));

                                           JScrollPane scrollPane = new JScrollPane(book_list);

                                           f.add(scrollPane);
                                           f.setSize(800, 400);
                                           f.setVisible(true);
                                           f.setLocationRelativeTo(null);
                                       } catch (SQLException e1) {
                                           // TODO Auto-generated catch block
                                           JOptionPane.showMessageDialog(null, e1);
                                       }

                                   }
                               }
        );

        JButton users = new JButton("Cek User");
        users.setBounds(150, 20, 120, 25);
        users.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {

                                        JFrame f = new JFrame("Daftar User");


                                        Connection connection = connect();
                                        String sql = "select * from users";
                                        try {
                                            Statement stmt = connection.createStatement();
                                            stmt.executeUpdate("USE LIBRARY");
                                            stmt = connection.createStatement();
                                            ResultSet rs = stmt.executeQuery(sql);
                                            JTable book_list = new JTable();
                                            book_list.setModel(DbUtils.resultSetToTableModel(rs));
                                            JScrollPane scrollPane = new JScrollPane(book_list);

                                            f.add(scrollPane);
                                            f.setSize(800, 400);
                                            f.setVisible(true);
                                            f.setLocationRelativeTo(null);
                                        } catch (SQLException e1) {
                                            // TODO Auto-generated catch block
                                            JOptionPane.showMessageDialog(null, e1);
                                        }


                                    }
                                }
        );

        JButton issued = new JButton("Cek Pinjaman Buku");
        issued.setBounds(280, 20, 160, 25);
        issued.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e) {

                                         JFrame f = new JFrame("Daftar Pinjaman");


                                         Connection connection = connect();
                                         String sql = "select * from issued";
                                         try {
                                             Statement stmt = connection.createStatement();
                                             stmt.executeUpdate("USE LIBRARY");
                                             stmt = connection.createStatement();
                                             ResultSet rs = stmt.executeQuery(sql);
                                             JTable book_list = new JTable();
                                             book_list.setModel(DbUtils.resultSetToTableModel(rs));

                                             JScrollPane scrollPane = new JScrollPane(book_list);

                                             f.add(scrollPane);
                                             f.setSize(800, 400);
                                             f.setVisible(true);
                                             f.setLocationRelativeTo(null);
                                         } catch (SQLException e1) {
                                             // TODO Auto-generated catch block
                                             JOptionPane.showMessageDialog(null, e1);
                                         }

                                     }
                                 }
        );


        JButton add_user = new JButton("Tambah User");
        add_user.setBounds(20, 60, 120, 25);

        add_user.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFrame g = new JFrame("Masukkan Detail User");
                JLabel l1, l2;
                l1 = new JLabel("Username");
                l1.setBounds(30, 15, 100, 30);


                l2 = new JLabel("Password");
                l2.setBounds(30, 50, 100, 30);

                JTextField F_user = new JTextField();
                F_user.setBounds(110, 15, 200, 30);

                JPasswordField F_pass = new JPasswordField();
                F_pass.setBounds(110, 50, 200, 30);

                JRadioButton a1 = new JRadioButton("Admin");
                a1.setBounds(55, 80, 200, 30);

                JRadioButton a2 = new JRadioButton("User");
                a2.setBounds(130, 80, 200, 30);

                ButtonGroup bg = new ButtonGroup();
                bg.add(a1);
                bg.add(a2);


                JButton create = new JButton("Buat!");
                create.setBounds(130, 130, 80, 25);
                create.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {

                        String username = F_user.getText();
                        String password = F_pass.getText();
                        Boolean admin = false;

                        if (a1.isSelected()) {
                            admin = true;
                        }

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            stmt.executeUpdate("INSERT INTO USERS(USERNAME,PASSWORD,ADMIN) VALUES ('" + username + "','" + password + "'," + admin + ")");
                            JOptionPane.showMessageDialog(null, "User berhasil dibuat!");
                            g.dispose();

                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });


                g.add(create);
                g.add(a2);
                g.add(a1);
                g.add(l1);
                g.add(l2);
                g.add(F_user);
                g.add(F_pass);
                g.setSize(350, 200);
                g.setLayout(null);
                g.setVisible(true);
                g.setLocationRelativeTo(null);


            }
        });


        JButton add_book = new JButton("Tambah Buku");
        add_book.setBounds(150, 60, 120, 25);

        add_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFrame g = new JFrame("Masukkan Detail Buku");
                JLabel l1, l2, l3;
                l1 = new JLabel("Judul");
                l1.setBounds(30, 15, 100, 30);


                l2 = new JLabel("Genre");
                l2.setBounds(30, 53, 100, 30);

                l3 = new JLabel("Harga");
                l3.setBounds(30, 90, 100, 30);

                JTextField F_name = new JTextField();
                F_name.setBounds(110, 15, 200, 30);

                JTextField F_genre = new JTextField();
                F_genre.setBounds(110, 53, 200, 30);

                JTextField F_price = new JTextField();
                F_price.setBounds(110, 90, 200, 30);


                JButton create = new JButton("Submit");
                create.setBounds(130, 130, 80, 25);
                create.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        String name = F_name.getText();
                        String genre = F_genre.getText();
                        String price = F_price.getText();
                        int price_int = Integer.parseInt(price);

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            stmt.executeUpdate("INSERT INTO BOOKS(NAME,GENRE,PRICE) VALUES ('" + name + "','" + genre + "'," + price_int + ")");
                            JOptionPane.showMessageDialog(null, "Buku berhasil ditambahkan!");
                            g.dispose();

                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });

                g.add(l3);
                g.add(create);
                g.add(l1);
                g.add(l2);
                g.add(F_name);
                g.add(F_genre);
                g.add(F_price);
                g.setSize(350, 200);
                g.setLayout(null);
                g.setVisible(true);
                g.setLocationRelativeTo(null);

            }
        });


        JButton issue_book = new JButton("Pinjam Buku");
        issue_book.setBounds(450, 20, 120, 25);

        issue_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame g = new JFrame("Masukkan Detail");
                JLabel l1, l2, l3, l4;
                l1 = new JLabel("ID Buku (BID)");
                l1.setBounds(30, 15, 100, 30);


                l2 = new JLabel("ID User (UID)");
                l2.setBounds(30, 53, 100, 30);

                l3 = new JLabel("Period (hari)");
                l3.setBounds(30, 90, 100, 30);

                l4 = new JLabel("Tanggal (DD-MM-YYYY)");
                l4.setBounds(30, 127, 150, 30);

                JTextField F_bid = new JTextField();
                F_bid.setBounds(110, 15, 200, 30);


                JTextField F_uid = new JTextField();
                F_uid.setBounds(110, 53, 200, 30);

                JTextField F_period = new JTextField();
                F_period.setBounds(110, 90, 200, 30);

                JTextField F_issue = new JTextField();
                F_issue.setBounds(180, 130, 130, 30);


                JButton create = new JButton("Pinjam!");
                create.setBounds(130, 170, 80, 25);
                create.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {

                        String uid = F_uid.getText();
                        String bid = F_bid.getText();
                        String period = F_period.getText();
                        String issued_date = F_issue.getText();

                        int period_int = Integer.parseInt(period);

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            stmt.executeUpdate("INSERT INTO ISSUED(UID,BID,ISSUED_DATE,PERIOD) VALUES ('" + uid + "','" + bid + "','" + issued_date + "'," + period_int + ")");
                            JOptionPane.showMessageDialog(null, "Buku terpinjam.");
                            g.dispose();

                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });


                g.add(l3);
                g.add(l4);
                g.add(create);
                g.add(l1);
                g.add(l2);
                g.add(F_uid);
                g.add(F_bid);
                g.add(F_period);
                g.add(F_issue);
                g.setSize(350, 250);
                g.setLayout(null);
                g.setVisible(true);
                g.setLocationRelativeTo(null);


            }
        });


        JButton return_book = new JButton("Pengembalian Buku");
        return_book.setBounds(280, 60, 160, 25);

        return_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFrame g = new JFrame("Masukkan Detail");
                JLabel l1, l2, l3, l4;
                l1 = new JLabel("ID Peminjaman (IID)");
                l1.setBounds(30, 15, 170, 30);


                l4 = new JLabel("Tanggal (DD-MM-YYYY)");
                l4.setBounds(30, 50, 150, 30);

                JTextField F_iid = new JTextField();
                F_iid.setBounds(230, 15, 80, 30);


                JTextField F_return = new JTextField();
                F_return.setBounds(180, 50, 130, 30);


                JButton create = new JButton("Submit");
                create.setBounds(130, 170, 80, 25);
                create.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {

                        String iid = F_iid.getText();
                        String return_date = F_return.getText();

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            String date1 = null;
                            String date2 = return_date;

                            ResultSet rs = stmt.executeQuery("SELECT ISSUED_DATE FROM ISSUED WHERE IID=" + iid);
                            while (rs.next()) {
                                date1 = rs.getString(1);

                            }

                            try {
                                Date date_1 = new SimpleDateFormat("dd-MM-yyyy").parse(date1);
                                Date date_2 = new SimpleDateFormat("dd-MM-yyyy").parse(date2);
                                long diff = date_2.getTime() - date_1.getTime();
                                ex.days = (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));


                            } catch (ParseException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }

                            stmt.executeUpdate("UPDATE ISSUED SET RETURN_DATE='" + return_date + "' WHERE IID=" + iid);
                            g.dispose();


                            Connection connection1 = connect();
                            Statement stmt1 = connection1.createStatement();
                            stmt1.executeUpdate("USE LIBRARY");
                            ResultSet rs1 = stmt1.executeQuery("SELECT PERIOD FROM ISSUED WHERE IID=" + iid);
                            String diff = null;
                            while (rs1.next()) {
                                diff = rs1.getString(1);

                            }
                            int diff_int = Integer.parseInt(diff);
                            if (ex.days >= diff_int) {

                                //System.out.println(ex.days);
                                int fine = (ex.days - diff_int) * 10;
                                stmt1.executeUpdate("UPDATE ISSUED SET FINE=" + fine + " WHERE IID=" + iid);
                                String fine_str = ("Denda: " + fine + "¥");
                                JOptionPane.showMessageDialog(null, fine_str);

                            }

                            JOptionPane.showMessageDialog(null, "Buku Berhasil Dikembalikan!");

                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });
                g.add(l4);
                g.add(create);
                g.add(l1);
                g.add(F_iid);
                g.add(F_return);
                g.setSize(350, 250);
                g.setLayout(null);
                g.setVisible(true);
                g.setLocationRelativeTo(null);
            }
        });

        f.add(create);
        f.add(return_book);
        f.add(issue_book);
        f.add(add_book);
        f.add(issued);
        f.add(users);
        f.add(view);
        f.add(add_user);
        f.add(close);
        f.setSize(600, 200);
        f.setLayout(null);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setResizable(false);

    }
}