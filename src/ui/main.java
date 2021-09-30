/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Color;
import java.awt.Component;
import static java.lang.Thread.sleep;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import config.Database;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author user
 */
public class main extends javax.swing.JFrame {

    private String SQL;
    Connection DB;
    PreparedStatement pst, pst2;
    ResultSet rst;
    int istok, istok2, iharga, ijumlah, kstok, tstok;
    String harga, barang, dbarang, KD, ssub;

    //Currency Rupiah
    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

    /**
     * Creates new form main
     */
    public main() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setResizable(false);
        DB = new Database().connect();
        autonumber();
        delay();
        profile_img("petugas");
        txt_jumlah.setEnabled(false);
        txt_total.setEnabled(false);
        txt_kembalian.setEnabled(false);
        textfield_jam.setEnabled(false);
        textfield_tgl.setEnabled(false);
        txt_bayar.setEnabled(false);
        alert.setVisible(false);

        //Currency Rupiah
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
    }

    public void profile_img(String img) {
        profile_img.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/" + img + ".png")));
    }

    public void cari_barang() {
        try {
            SQL = "SELECT * FROM barang WHERE id LIKE '%" + txt_cari.getText() + "%'";
            pst = DB.prepareStatement(SQL);
            rst = pst.executeQuery();
            tbl_barang.setModel(DbUtils.resultSetToTableModel(rst));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void autonumber() {
        txt_kd_transaksi.setEnabled(false);
        try {
            SQL = "SELECT MAX(RIGHT(id,3)) AS NO FROM transaksi";
            pst = DB.prepareStatement(SQL);
            rst = pst.executeQuery();
            while (rst.next()) {
                if (rst.first() == false) {
                    txt_kd_transaksi.setText("001");
                } else {
                    rst.last();
                    int auto_id = rst.getInt(1) + 1;
                    String no = String.valueOf(auto_id);
                    int NomorJual = no.length();
                    for (int j = 0; j < 3 - NomorJual; j++) {
                        no = "0" + no;
                    }
                    txt_kd_transaksi.setText(no);
                }
            }
            rst.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void subtotal() {
        int jumlah, sub;
        jumlah = Integer.parseInt(txt_jumlah.getText());
        sub = (jumlah * iharga);
        ssub = String.valueOf(sub);
    }

    public void kurangi_stok() {
        int qty;
        qty = Integer.parseInt(txt_jumlah.getText());
        if (istok < qty) {
            JOptionPane.showMessageDialog(null, "Stok tidak cukup!");
        } else {
            kstok = istok - qty;
        }

    }

    public void detail() {
        try {
            String Kode_detail = txt_kd_transaksi.getText();
            String KD = Kode_detail;
            SQL = "select * from det_transaksi where id='" + KD + "'";
            pst = DB.prepareStatement(SQL);
            rst = pst.executeQuery();
            tbl_detail.setModel(DbUtils.resultSetToTableModel(rst));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void sum() {
        int totalBiaya = 0;
        int subtotal;
        DefaultTableModel dataModel = (DefaultTableModel) tbl_detail.getModel();
        int jumlah = tbl_detail.getRowCount();
        for (int i = 0; i < jumlah; i++) {
            subtotal = Integer.parseInt(dataModel.getValueAt(i, 4).toString());
            totalBiaya += subtotal;
        }
        txt_total.setText(String.valueOf(totalBiaya));
    }

    public void clear() {
        txt_jumlah.setText("");
    }

    public void tambah_stok() {
        tstok = ijumlah + istok2;
        try {
            String update = "update barang set stok='" + tstok + "' where id='" + barang + "'";
            pst2 = DB.prepareStatement(update);
            pst2.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void ambil_stok() {
        try {
            SQL = "select * from barang where id='" + barang + "'";
            pst = DB.prepareStatement(SQL);
            rst = pst.executeQuery();
            if (rst.next()) {
                String stok = rst.getString(("stok"));
                istok2 = Integer.parseInt(stok);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void total() {
        int total, bayar, kembali;
        total = Integer.parseInt(txt_bayar.getText());
        bayar = Integer.parseInt(txt_total.getText());
        kembali = total - bayar;
        String ssub = String.valueOf(kembali);
        txt_kembalian.setText(ssub);
    }

    public void simpan() {
        String tgl = textfield_tgl.getText();
        String jam = textfield_jam.getText();
        try {
            SQL = "insert into transaksi (id,id_detail,id_user,tgl,jam,total) value (?,?,?,?,?,?)";
            pst = DB.prepareStatement(SQL);
            pst.setString(1, txt_kd_transaksi.getText());
            pst.setString(2, KD);
            pst.setString(3, lbl_id.getText());
            pst.setString(4, tgl);
            pst.setString(5, jam);
            pst.setString(6, txt_total.getText());
            pst.execute();
            new loader(null, true).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void delay() {
        Thread clock = new Thread() {
            public void run() {
                for (;;) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat format2 = new SimpleDateFormat("EEEE / dd-MM-yyyy");
                    SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd");
                    jam.setText(format.format(cal.getTime()));
                    tgl.setText(format2.format(cal.getTime()));

                    textfield_jam.setText(format.format(cal.getTime()));
                    textfield_tgl.setText(format3.format(cal.getTime()));

                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        clock.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bodyPanel = new javax.swing.JPanel();
        sidebarPanel = new javax.swing.JPanel();
        barangPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        transaksiPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        userPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        profile_img = new javax.swing.JLabel();
        lbl_nama_user = new javax.swing.JLabel();
        lbl_role = new javax.swing.JLabel();
        logoutPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        laporanPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        homePanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        DP = new icon.DP();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tgl = new javax.swing.JLabel();
        jam = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        transaksi = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_cari = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_barang = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        txt_jumlah = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        txt_kd_transaksi = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_detail = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        txt_total = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txt_bayar = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txt_kembalian = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        textfield_jam = new javax.swing.JTextField();
        textfield_tgl = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        lbl_id = new javax.swing.JLabel();
        alert = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Aplikasi Kasir");

        bodyPanel.setBackground(new java.awt.Color(255, 255, 255));

        sidebarPanel.setBackground(new java.awt.Color(9, 132, 227));

        barangPanel.setBackground(new java.awt.Color(9, 132, 227));
        barangPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                barangPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                barangPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                barangPanelMouseExited(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-shopping-bag-full-48.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Data Barang");

        javax.swing.GroupLayout barangPanelLayout = new javax.swing.GroupLayout(barangPanel);
        barangPanel.setLayout(barangPanelLayout);
        barangPanelLayout.setHorizontalGroup(
            barangPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barangPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addContainerGap(66, Short.MAX_VALUE))
        );
        barangPanelLayout.setVerticalGroup(
            barangPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        transaksiPanel.setBackground(new java.awt.Color(9, 132, 227));
        transaksiPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                transaksiPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                transaksiPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                transaksiPanelMouseExited(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-shop-48.png"))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Transaksi");

        javax.swing.GroupLayout transaksiPanelLayout = new javax.swing.GroupLayout(transaksiPanel);
        transaksiPanel.setLayout(transaksiPanelLayout);
        transaksiPanelLayout.setHorizontalGroup(
            transaksiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transaksiPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        transaksiPanelLayout.setVerticalGroup(
            transaksiPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        userPanel.setBackground(new java.awt.Color(9, 132, 227));
        userPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                userPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                userPanelMouseExited(evt);
            }
        });

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-add-user-male-48.png"))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Data User");

        javax.swing.GroupLayout userPanelLayout = new javax.swing.GroupLayout(userPanel);
        userPanel.setLayout(userPanelLayout);
        userPanelLayout.setHorizontalGroup(
            userPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addContainerGap(91, Short.MAX_VALUE))
        );
        userPanelLayout.setVerticalGroup(
            userPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        profile_img.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/petugas.png"))); // NOI18N

        lbl_nama_user.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        lbl_nama_user.setForeground(new java.awt.Color(255, 255, 255));
        lbl_nama_user.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_nama_user.setText("Nama Petugas");

        lbl_role.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        lbl_role.setForeground(new java.awt.Color(255, 255, 255));
        lbl_role.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_role.setText("Role");

        logoutPanel.setBackground(new java.awt.Color(9, 132, 227));
        logoutPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutPanelMouseExited(evt);
            }
        });

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-imac-exit-48.png"))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Logout");

        javax.swing.GroupLayout logoutPanelLayout = new javax.swing.GroupLayout(logoutPanel);
        logoutPanel.setLayout(logoutPanelLayout);
        logoutPanelLayout.setHorizontalGroup(
            logoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoutPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        logoutPanelLayout.setVerticalGroup(
            logoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        laporanPanel.setBackground(new java.awt.Color(9, 132, 227));
        laporanPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                laporanPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                laporanPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                laporanPanelMouseExited(evt);
            }
        });

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-pdf-48.png"))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Generate Laporan");

        javax.swing.GroupLayout laporanPanelLayout = new javax.swing.GroupLayout(laporanPanel);
        laporanPanel.setLayout(laporanPanelLayout);
        laporanPanelLayout.setHorizontalGroup(
            laporanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(laporanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        laporanPanelLayout.setVerticalGroup(
            laporanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        homePanel.setBackground(new java.awt.Color(9, 132, 227));
        homePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homePanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homePanelMouseExited(evt);
            }
        });

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-home-48.png"))); // NOI18N

        jLabel15.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Home");

        javax.swing.GroupLayout homePanelLayout = new javax.swing.GroupLayout(homePanel);
        homePanel.setLayout(homePanelLayout);
        homePanelLayout.setHorizontalGroup(
            homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        homePanelLayout.setVerticalGroup(
            homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(profile_img)
                    .addComponent(lbl_nama_user, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                    .addComponent(lbl_role, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laporanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(barangPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(transaksiPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(userPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(logoutPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(homePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(profile_img)
                .addGap(24, 24, 24)
                .addComponent(lbl_nama_user, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_role)
                .addGap(61, 61, 61)
                .addComponent(homePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(barangPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(transaksiPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(laporanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logoutPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        mainPanel.setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(9, 132, 227));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-clock-48.png"))); // NOI18N

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icons8-calendar-16-48.png"))); // NOI18N

        tgl.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        tgl.setForeground(new java.awt.Color(255, 255, 255));
        tgl.setText("Tanggal");

        jam.setBackground(new java.awt.Color(204, 204, 204));
        jam.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jam.setForeground(new java.awt.Color(255, 255, 255));
        jam.setText("Jam");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tgl, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tgl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(11, 11, 11))
        );

        jPanel2.setBackground(new java.awt.Color(52, 73, 94));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );

        DP.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        DP.setLayer(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout DPLayout = new javax.swing.GroupLayout(DP);
        DP.setLayout(DPLayout);
        DPLayout.setHorizontalGroup(
            DPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DPLayout.createSequentialGroup()
                .addContainerGap(613, Short.MAX_VALUE)
                .addGroup(DPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        DPLayout.setVerticalGroup(
            DPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DPLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(780, Short.MAX_VALUE))
        );

        mainPanel.add(DP, "card2");

        transaksi.setBackground(new java.awt.Color(255, 255, 255));

        jLabel13.setFont(new java.awt.Font("Century Gothic", 1, 36)); // NOI18N
        jLabel13.setText("Transaksi");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setText("Masukkan Kode Barang");

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton1.setText("Cari");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tbl_barang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Kode Barang", "Nama Barang", "Harga", "Satuan", "Stok"
            }
        ));
        tbl_barang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_barangMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_barang);

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setText("Jumlah");

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton2.setText("+");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setText("Kode Transaksi");

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setText("Data Barang");

        tbl_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_detail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_detailMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_detail);

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setText("Total");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setText("Bayar");

        txt_bayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_bayarKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_bayarKeyTyped(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel22.setText("Kembalian");

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel23.setText("Hapus");

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton3.setText("-");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton4.setText("Bayar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton5.setText("New");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        textfield_jam.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        textfield_jam.setText("Jam");

        textfield_tgl.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        textfield_tgl.setText("Tgl");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("User ID:");

        lbl_id.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbl_id.setText("ID");

        alert.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        alert.setForeground(new java.awt.Color(255, 0, 51));
        alert.setText("jLabel25");

        javax.swing.GroupLayout transaksiLayout = new javax.swing.GroupLayout(transaksi);
        transaksi.setLayout(transaksiLayout);
        transaksiLayout.setHorizontalGroup(
            transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transaksiLayout.createSequentialGroup()
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(transaksiLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(transaksiLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(textfield_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(transaksiLayout.createSequentialGroup()
                                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lbl_id, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(transaksiLayout.createSequentialGroup()
                                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_kd_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel16)
                                    .addGroup(transaksiLayout.createSequentialGroup()
                                        .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel19)
                                            .addGroup(transaksiLayout.createSequentialGroup()
                                                .addComponent(txt_cari, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jButton1))
                                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
                                            .addComponent(jScrollPane2))
                                        .addGap(48, 48, 48)
                                        .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(transaksiLayout.createSequentialGroup()
                                                .addComponent(txt_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jButton2))
                                            .addComponent(jLabel23)
                                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel17))))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transaksiLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(textfield_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(transaksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(transaksiLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt_total, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(transaksiLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(18, 18, 18)
                        .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(transaksiLayout.createSequentialGroup()
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(23, 23, 23)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_bayar))))
                .addGap(63, 63, 63)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(transaksiLayout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_kembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(alert, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        transaksiLayout.setVerticalGroup(
            transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transaksiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textfield_jam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(transaksiLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(46, 46, 46)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                            .addComponent(txt_cari))
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(transaksiLayout.createSequentialGroup()
                        .addComponent(textfield_tgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(lbl_id))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(35, 35, 35)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_kd_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(transaksiLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(transaksiLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_total, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alert, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(transaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53))
        );

        mainPanel.add(transaksi, "card3");

        javax.swing.GroupLayout bodyPanelLayout = new javax.swing.GroupLayout(bodyPanel);
        bodyPanel.setLayout(bodyPanelLayout);
        bodyPanelLayout.setHorizontalGroup(
            bodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addComponent(sidebarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        bodyPanelLayout.setVerticalGroup(
            bodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bodyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sidebarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void barangPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barangPanelMouseEntered
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(116, 185, 255));
    }//GEN-LAST:event_barangPanelMouseEntered

    private void barangPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barangPanelMouseExited
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(9, 132, 227));
    }//GEN-LAST:event_barangPanelMouseExited

    private void barangPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barangPanelMouseClicked
        // TODO add your handling code here:
        // remove panel
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        // add panel
        mainPanel.add(DP);
        mainPanel.repaint();
        mainPanel.revalidate();

        barang b = new barang();
        DP.add(b);
        b.setVisible(true);
    }//GEN-LAST:event_barangPanelMouseClicked

    private void transaksiPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_transaksiPanelMouseEntered
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(116, 185, 255));
    }//GEN-LAST:event_transaksiPanelMouseEntered

    private void transaksiPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_transaksiPanelMouseExited
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(9, 132, 227));
    }//GEN-LAST:event_transaksiPanelMouseExited

    private void userPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userPanelMouseEntered
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(116, 185, 255));
    }//GEN-LAST:event_userPanelMouseEntered

    private void userPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userPanelMouseExited
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(9, 132, 227));
    }//GEN-LAST:event_userPanelMouseExited

    private void laporanPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_laporanPanelMouseEntered
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(116, 185, 255));
    }//GEN-LAST:event_laporanPanelMouseEntered

    private void laporanPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_laporanPanelMouseExited
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(9, 132, 227));
    }//GEN-LAST:event_laporanPanelMouseExited

    private void logoutPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutPanelMouseEntered
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(116, 185, 255));
    }//GEN-LAST:event_logoutPanelMouseEntered

    private void logoutPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutPanelMouseExited
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(9, 132, 227));
    }//GEN-LAST:event_logoutPanelMouseExited

    private void logoutPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutPanelMouseClicked
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(this, "Anda yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == 0) {
            this.dispose();
            new login().setVisible(true);
        }
    }//GEN-LAST:event_logoutPanelMouseClicked

    private void transaksiPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_transaksiPanelMouseClicked
        // TODO add your handling code here:
        // remove panel
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        // add panel
        mainPanel.add(transaksi);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_transaksiPanelMouseClicked

    private void userPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userPanelMouseClicked
        // TODO add your handling code here:
        // remove panel
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        // add panel
        mainPanel.add(DP);
        mainPanel.repaint();
        mainPanel.revalidate();

        user u = new user();
        DP.add(u);
        u.setVisible(true);
    }//GEN-LAST:event_userPanelMouseClicked

    private void laporanPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_laporanPanelMouseClicked
        // TODO add your handling code here:
        java.sql.Connection conn = new Database().connect();
        try {
            File file = new File("src/laporan/Laporan_Transaksi.jrxml");
            JasperDesign jasperDesign = JRXmlLoader.load(file);
            SQL = "SELECT transaksi.`id` AS kd_transaksi,`user`.`nama` AS nama_petugas, \n"
                    + "transaksi.`tgl`, transaksi.`jam`, barang.`nama_barang`,\n"
                    + "det_transaksi.`jumlah` AS qty, det_transaksi.`subtotal` ,transaksi.`total` AS total_belanja \n"
                    + "FROM transaksi\n"
                    + "INNER JOIN det_transaksi ON transaksi.`id_detail` = det_transaksi.`id`\n"
                    + "INNER JOIN `user` ON transaksi.`id_user` = `user`.`id`\n"
                    + "INNER JOIN barang ON det_transaksi.`id_barang` = barang.`id`";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(SQL);
            jasperDesign.setQuery(newQuery);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_laporanPanelMouseClicked

    private void homePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homePanelMouseClicked
        // TODO add your handling code here:
        // remove panel
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        // add panel
        mainPanel.add(DP);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_homePanelMouseClicked

    private void homePanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homePanelMouseEntered
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(116, 185, 255));
    }//GEN-LAST:event_homePanelMouseEntered

    private void homePanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homePanelMouseExited
        // TODO add your handling code here:
        Component c = evt.getComponent();
        c.setBackground(new Color(9, 132, 227));
    }//GEN-LAST:event_homePanelMouseExited

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        cari_barang();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int qty;
        qty = Integer.parseInt(txt_jumlah.getText());
        if (istok < qty) {
            JOptionPane.showMessageDialog(null, "Stok tidak cukup!");
        } else if (istok == 0 && qty == 0) {
            JOptionPane.showMessageDialog(null, "Stok tidak cukup!");
        } else {
            kstok = istok - qty;
            subtotal();
            try {
                String Kode_detail = txt_kd_transaksi.getText();
                KD = Kode_detail;
                SQL = "insert into det_transaksi (id,id_barang,harga,jumlah,subtotal) value (?,?,?,?,?)";
                String update = "update barang set stok = '" + kstok + "' where id = '" + barang + "'";
                pst = DB.prepareStatement(SQL);
                pst2 = DB.prepareStatement(update);
                pst.setString(1, KD);
                pst.setString(2, barang);
                pst.setString(3, harga);
                pst.setString(4, txt_jumlah.getText());
                pst.setString(5, ssub);
                pst.execute();
                pst2.execute();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
            detail();
            sum();
            cari_barang();
            clear();

            txt_bayar.setEnabled(true);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void tbl_barangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_barangMouseClicked
        // TODO add your handling code here:
        txt_jumlah.setEnabled(true);
        try {
            int row = tbl_barang.getSelectedRow();
            String tabel_klik = (tbl_barang.getModel().getValueAt(row, 0).toString());
            SQL = "select * from barang where id = '" + tabel_klik + "'";
            pst = DB.prepareStatement(SQL);
            rst = pst.executeQuery();
            if (rst.next()) {
                barang = rst.getString(("id"));
                String stok = rst.getString(("stok"));
                istok = Integer.parseInt(stok);
                harga = rst.getString(("harga"));
                iharga = Integer.parseInt(harga);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_tbl_barangMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        try {
            String sql = "delete from det_transaksi where id_barang=?";
            pst = DB.prepareStatement(sql);
            pst.setString(1, dbarang);
            pst.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        detail();
        sum();
        tambah_stok();
        cari_barang();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void tbl_detailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_detailMouseClicked
        // TODO add your handling code here:
        try {
            int row = tbl_detail.getSelectedRow();
            dbarang = (tbl_detail.getModel().getValueAt(row, 1).toString());
            SQL = "select * from det_transaksi where id_barang = '" + dbarang + "'";
            pst = DB.prepareStatement(SQL);
            rst = pst.executeQuery();
            if (rst.next()) {
                String jumlah = rst.getString(("jumlah"));
                ijumlah = Integer.parseInt(jumlah);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        ambil_stok();
    }//GEN-LAST:event_tbl_detailMouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        total();
        simpan();
        detail();
        txt_total.setText("");
        txt_bayar.setText("");
        txt_kembalian.setText("");
        txt_cari.setText("");
        cari_barang();

        java.sql.Connection conn = new Database().connect();
        HashMap map = new HashMap();
        map.put("id_transaksi", KD);

        File lokasi = new File("src/laporan/Laporan_Struk.jrxml");
        try {
            JasperDesign jd = JRXmlLoader.load(lokasi);
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, map, conn);
            JasperViewer.viewReport(jp, false);
        } catch (JRException ex) {
        }

        autonumber();
        DefaultTableModel dtm = (DefaultTableModel) tbl_barang.getModel();
        dtm.setRowCount(0);
        txt_cari.setText("");
        txt_bayar.setEnabled(false);
        txt_kembalian.setText("");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel dtm = (DefaultTableModel) tbl_barang.getModel();
        dtm.setRowCount(0);
        txt_cari.setText("");

        txt_bayar.setEnabled(false);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void txt_bayarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bayarKeyTyped
        // TODO add your handling code here:
        char enter = evt.getKeyChar();
        if (Character.isAlphabetic(enter)) {
            evt.consume();
            alert.setText("Inputan harus angka");
            alert.setVisible(true);
        } else {
            alert.setVisible(false);
        }
    }//GEN-LAST:event_txt_bayarKeyTyped

    private void txt_bayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bayarKeyReleased
        // TODO add your handling code here:
        if (!txt_bayar.getText().equalsIgnoreCase("")) {
            int bayar, total, kem;
            total = Integer.parseInt(txt_total.getText());
            bayar = Integer.parseInt(txt_bayar.getText());
            kem = bayar - total;

            if (kem < 0) {
                alert.setText("Jumlah uang kurang");
                alert.setVisible(true);
                jButton4.setEnabled(false);
            } else {
                alert.setVisible(false);
                txt_kembalian.setText(String.valueOf(+kem));
                txt_kembalian.setText(kursIndonesia.format(kem));
                jButton4.setEnabled(true);

            }
        } else {
            txt_kembalian.setText("");
        }
    }//GEN-LAST:event_txt_bayarKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.jPanel1javase/tutojPanel1/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane DP;
    private javax.swing.JLabel alert;
    private javax.swing.JPanel barangPanel;
    private javax.swing.JPanel bodyPanel;
    private javax.swing.JPanel homePanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jam;
    public javax.swing.JPanel laporanPanel;
    public javax.swing.JLabel lbl_id;
    public javax.swing.JLabel lbl_nama_user;
    public javax.swing.JLabel lbl_role;
    private javax.swing.JPanel logoutPanel;
    private javax.swing.JPanel mainPanel;
    public javax.swing.JLabel profile_img;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTable tbl_barang;
    private javax.swing.JTable tbl_detail;
    private javax.swing.JTextField textfield_jam;
    private javax.swing.JTextField textfield_tgl;
    private javax.swing.JLabel tgl;
    private javax.swing.JPanel transaksi;
    private javax.swing.JPanel transaksiPanel;
    private javax.swing.JTextField txt_bayar;
    private javax.swing.JTextField txt_cari;
    private javax.swing.JTextField txt_jumlah;
    private javax.swing.JTextField txt_kd_transaksi;
    private javax.swing.JTextField txt_kembalian;
    private javax.swing.JTextField txt_total;
    public javax.swing.JPanel userPanel;
    // End of variables declaration//GEN-END:variables
}
