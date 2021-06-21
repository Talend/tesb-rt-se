/*
 * #%L
 * App Reservation Basic
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.client.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.talend.esb.client.model.CarReserveModel;
import org.talend.esb.client.model.CarSearchModel;
import org.talend.services.crm.types.CustomerDetailsType;
import org.talend.services.reservation.types.ConfirmationType;
import org.talend.services.reservation.types.RESCarType;
import org.talend.services.reservation.types.RESStatusType;

public class CarRentalClientGui extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String CONFIRM = "CONFIRM";
	private static final String SELECT = "SELECT";
	private static final String FIND = "FIND";


	class CarTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		private final String[] COL_NAMES = {
			Messages.CarRentalClient_Brand
			, Messages.CarRentalClient_Model
			, Messages.CarRentalClient_BookingClass
			, Messages.CarRentalClient_DayRate
			, Messages.CarRentalClient_WeekEndRate
			, Messages.CarRentalClient_Insurance};

		List<RESCarType> myCars = new ArrayList<RESCarType>();

		public void setData(List<RESCarType> cars) {
			myCars = cars;
			this.fireTableDataChanged();
		}

		public int getRowCount() {
			return myCars.size();
		}

		public int getColumnCount() {
			return COL_NAMES.length;
		}

	    public String getColumnName(int column) {
	    	return COL_NAMES[column];
	    }

		public Object getValueAt(int rowIndex, int columnIndex) {
			RESCarType c = myCars.get(rowIndex);

			switch(columnIndex) {
				case 0: return c.getBrand();
				case 1: return c.getDesignModel();
				case 2: return c.getClazz();
				case 3: return c.getRateDay();
				case 4: return c.getRateWeekend();
				case 5: return c.getSecurityGuarantee();
			}
			return null;
		}

	}

	private CarSearchModel search;
	private CarReserveModel reserve;
	private JFrame appFrame;
	private CardLayout cardlist;
	private JPanel content;
	private JPanel findPanel;
	private JButton cmdFind;
	private JButton cmdFindCancel;
	private JButton cmdSelect;
	private JButton cmdSelectBack;
	private JButton cmdSelectCancel;
	private JButton cmdClose;
	private JComboBox cUser;
	private JFormattedTextField tPickupDate;
	private JFormattedTextField tReturnDate;
	private JPanel selectPanel;
	private JPanel confirmPanel;
	private CarTableModel ctm = new CarTableModel();
	private JTextField tReservationId;
	private JLabel lStatus;
	private JTextField tName;
	private JTextField tEMail;
	private JTextField tCity;
	private JTextField tStatus;
	private JTextField tBrand;
	private JTextField tModel;
	private JTextField tPickup;
	private JTextField tReturn;
	private JTextField tCredits;
	private JTextField tDaily;
	private JTextField tWeekEnd;
	private JTable selectTable;

	public CarRentalClientGui(CarSearchModel searchModel, CarReserveModel reserveModel) {
		this.search = searchModel;
		this.reserve = reserveModel;
		this.setLayout(new BorderLayout(5, 5));
		this.setBackground(Color.WHITE);

        add(createHeaderPanel(), BorderLayout.NORTH);

        findPanel = createFindPanel();
        selectPanel = createSelectionPanel();
        confirmPanel = createConfirmationPanel();

        content = createPanel();
        cardlist = new CardLayout();
        content.setLayout(cardlist);
        content.add(findPanel, FIND);
        content.add(selectPanel, SELECT);
        content.add(confirmPanel, CONFIRM);
        add(content, BorderLayout.CENTER);
	}

	private ImageIcon createImageIcon(String path) {
		URL imgURL = getClass().getClassLoader().getResource(path);
		return (imgURL != null) ? new ImageIcon(imgURL) : null;
	}


	private JPanel createPanel() {
		JPanel p = new JPanel(new BorderLayout(2, 2));
		p.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		p.setBackground(Color.WHITE);
		return p;
	}

	private GridBagConstraints createGridBagConstants() {
		return new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
				2, 2, 2), 2, 2);
	}

	private void addField(JPanel p, GridBagConstraints gbc, JLabel l, JComponent t, int x, int y) {
		gbc.gridx = x;
		gbc.gridy = y;
		p.add(l, gbc);
		++gbc.gridx;
		p.add(t, gbc);
	}


	private JPanel createHeaderPanel() {
		JPanel header = createPanel();
		header.add(new JLabel(Messages.CarRentalClient_Title), BorderLayout.WEST);
		header.add(new JLabel(createImageIcon("talend.gif")), BorderLayout.EAST);
		return header;
	}


	private JPanel createStepPanel(String stepImage) {
		JPanel imgPanel = createPanel();
		imgPanel.add(new JLabel(createImageIcon(stepImage)), BorderLayout.CENTER);
		return imgPanel;
	}


	private JPanel createFindInput() {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		JPanel searchInp = createPanel();
		searchInp.setLayout(new GridBagLayout());
		GridBagConstraints gbc = createGridBagConstants();

		JLabel lUser = new JLabel(Messages.CarRentalClient_User);
		cUser = new JComboBox(new String[] {"aebert", "jdoe", "bbrindle", "rlambert"});
		cUser.setSelectedIndex(0);
		cUser.setEditable(true);
		addField(searchInp, gbc, lUser, cUser, 1, 1);

		JLabel lPickupDate = new JLabel(Messages.CarRentalClient_Pickup);
		tPickupDate = new JFormattedTextField(df);
		tPickupDate.setValue(new Date());
		addField(searchInp, gbc, lPickupDate, tPickupDate, 1, 2);

		JLabel lReturnDate = new JLabel(Messages.CarRentalClient_Return);
		tReturnDate = new JFormattedTextField(df);
		tReturnDate.setValue(new Date());
		addField(searchInp, gbc, lReturnDate, tReturnDate, 1, 3);
		JPanel spacer = createPanel();
		spacer.setPreferredSize(new Dimension(0, 300));
		++gbc.gridy;
		searchInp.add(spacer, gbc);
		return searchInp;
	}


	private JPanel createFindPanel() {
		JPanel p = createPanel();
		p.add(createStepPanel("step1.gif"), BorderLayout.NORTH);
		p.add(createFindInput(), BorderLayout.CENTER);
		p.add(createFindCommands(), BorderLayout.SOUTH);
		return p;
	}


	private JPanel createFindCommands() {
		JPanel searchCmd = createPanel();
		searchCmd.setLayout(new BoxLayout(searchCmd, BoxLayout.LINE_AXIS));
		searchCmd.add(Box.createRigidArea(new Dimension(380, 0)));

		cmdFindCancel = new JButton(Messages.CarRentalClient_CmdCancel);
		cmdFindCancel.addActionListener(this);
		cmdFind = new JButton(Messages.CarRentalClient_CmdFind);
		cmdFind.addActionListener(this);
		searchCmd.add(cmdFindCancel);
		searchCmd.add(Box.createRigidArea(new Dimension(50, 0)));

		searchCmd.add(cmdFind);
		return searchCmd;
	}


	private JPanel createSelectionPanel() {
		JPanel select = createPanel();
		select.add(createStepPanel("step2.gif"), BorderLayout.NORTH);
		select.add(createSelectionInput(), BorderLayout.CENTER);
		select.add(createSelectionCommands(), BorderLayout.SOUTH);
		return select;
	}


	private JPanel createSelectionInput() {
		JPanel selectInp = createPanel();
		JPanel pInfo = createPanel();
		pInfo.add(new JLabel(Messages.CarRentalClient_Offering), BorderLayout.NORTH);
		pInfo.add(new JLabel(Messages.CarRentalClient_SelectInfo), BorderLayout.SOUTH);

		selectTable = new JTable(ctm);
		selectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(selectTable);

		selectInp.add(pInfo, BorderLayout.NORTH);
		selectInp.add(scrollPane, BorderLayout.SOUTH);
		return selectInp;
	}


	private JPanel createSelectionCommands() {
		JPanel selectCmd = createPanel();
		selectCmd.setLayout(new BoxLayout(selectCmd, BoxLayout.LINE_AXIS));
		cmdSelectBack = new JButton(Messages.CarRentalClient_CmdBack);
		cmdSelectBack.addActionListener(this);
		selectCmd.add(cmdSelectBack);
		selectCmd.add(Box.createRigidArea(new Dimension(240, 0)));

		cmdSelectCancel = new JButton(Messages.CarRentalClient_CmdCancel);
		cmdSelectCancel.addActionListener(this);
		cmdSelect = new JButton(Messages.CarRentalClient_CmdReserve);
		cmdSelect.addActionListener(this);
		selectCmd.add(cmdFindCancel);
		selectCmd.add(Box.createRigidArea(new Dimension(25, 0)));

		selectCmd.add(cmdSelect);
		return selectCmd;
	}


	private JPanel createConfirmationPanel() {
		JPanel confirm = createPanel();
		confirm.add(createStepPanel("step3.gif"), BorderLayout.NORTH);
		confirm.add(createConfirmationContent(), BorderLayout.CENTER);
		confirm.add(createConfirmCommands(), BorderLayout.SOUTH);
		return confirm;
	}


	private JPanel createConfirmationContent() {
		JPanel confCont = createPanel();
		confCont.add(createConfirmationHeader(), BorderLayout.NORTH);

		JPanel confMain = createPanel();
		confMain.add(createCustomerDetails(), BorderLayout.NORTH);
		confMain.add(createCarDetails(), BorderLayout.CENTER);
		confMain.add(createReservationDetails(), BorderLayout.SOUTH);
		confCont.add(confMain, BorderLayout.CENTER);
		return confCont;
	}


	private JPanel createConfirmationHeader() {
		JPanel cHeader = createPanel();
		cHeader.setLayout(new GridBagLayout());
		GridBagConstraints gbc = createGridBagConstants();

		lStatus = new JLabel();
		JLabel lReservationId = new JLabel(Messages.CarRentalClient_ReservationID);
		tReservationId = new JTextField(15);
		tReservationId.setEditable(false);
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		cHeader.add(lStatus, gbc);
		gbc.gridwidth = 1;
		addField(cHeader, gbc, lReservationId, tReservationId, 0, 1);
		return cHeader;
	}


	private JPanel createCustomerDetails() {
		JPanel cDetails = createPanel();
		cDetails.setLayout(new GridBagLayout());
		cDetails.setBorder(BorderFactory.createTitledBorder(Messages.CarRentalClient_CustomerDetails));
		GridBagConstraints gbc = createGridBagConstants();

		JLabel lName = new JLabel(Messages.CarRentalClient_Name);
		tName = new JTextField(25);
		tName.setEditable(false);
		addField(cDetails, gbc, lName, tName, 1, 1);

		JLabel lEMail = new JLabel(Messages.CarRentalClient_eMail);
		tEMail = new JTextField(25);
		tEMail.setEditable(false);
		addField(cDetails, gbc, lEMail, tEMail, 1, 2);

		JLabel lCity = new JLabel(Messages.CarRentalClient_City);
		tCity = new JTextField(25);
		tCity.setEditable(false);
		addField(cDetails, gbc, lCity, tCity, 1, 3);

		JLabel lStatus = new JLabel(Messages.CarRentalClient_Status);
		tStatus = new JTextField(25);
		tStatus.setEditable(false);
		addField(cDetails, gbc, lStatus, tStatus, 1, 4);

		return cDetails;
	}


	private JPanel createCarDetails() {
		JPanel cDetails = createPanel();
		cDetails.setLayout(new GridBagLayout());
		cDetails.setBorder(BorderFactory.createTitledBorder(Messages.CarRentalClient_CarDetails));
		GridBagConstraints gbc = createGridBagConstants();

		JLabel lBrand = new JLabel(Messages.CarRentalClient_Brand);
		tBrand = new JTextField(25);
		tBrand.setEditable(false);
		addField(cDetails, gbc, lBrand, tBrand, 1, 1);

		JLabel lModel = new JLabel(Messages.CarRentalClient_Model);
		tModel = new JTextField(25);
		tModel.setEditable(false);
		addField(cDetails, gbc, lModel, tModel, 1, 2);

		return cDetails;
	}


	private JPanel createReservationDetails() {
		JPanel cDetails = createPanel();
		cDetails.setLayout(new GridBagLayout());
		cDetails.setBorder(BorderFactory.createTitledBorder(Messages.CarRentalClient_ReservationDetails));
		GridBagConstraints gbc = createGridBagConstants();

		JLabel lPickup = new JLabel(Messages.CarRentalClient_Pickup);
		tPickup = new JTextField(10);
		tPickup.setEditable(false);
		addField(cDetails, gbc, lPickup, tPickup, 1, 1);

		JLabel lReturn = new JLabel(Messages.CarRentalClient_Return);
		tReturn = new JTextField(10);
		tReturn.setEditable(false);
		addField(cDetails, gbc, lReturn, tReturn, 3, 1);


		JLabel lDaily = new JLabel(Messages.CarRentalClient_DayRate);
		tDaily = new JTextField(5);
		tDaily.setEditable(false);
		addField(cDetails, gbc, lDaily, tDaily, 1, 2);

		JLabel lWeekEnd = new JLabel(Messages.CarRentalClient_WeekEndRate);
		tWeekEnd = new JTextField(5);
		tWeekEnd.setEditable(false);
		addField(cDetails, gbc, lWeekEnd, tWeekEnd, 3, 2);

		JLabel lCredits = new JLabel(Messages.CarRentalClient_Credits);
		tCredits = new JTextField(7);
		tCredits.setEditable(false);
		addField(cDetails, gbc, lCredits, tCredits, 1, 3);

		return cDetails;
	}


	private JPanel createConfirmCommands() {
		JPanel confirmCmd = createPanel();
		confirmCmd.setLayout(new BoxLayout(confirmCmd, BoxLayout.LINE_AXIS));
		cmdClose = new JButton(Messages.CarRentalClient_CmdClose);
		cmdClose.addActionListener(this);

		confirmCmd.add(Box.createRigidArea(new Dimension(440, 0)));
		confirmCmd.add(cmdClose);
		return confirmCmd;
	}


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI(CarSearchModel searchModel, CarReserveModel reserveModel) {
        //Create and set up the window.
        JFrame appFrame = new JFrame(Messages.CarRentalClient_Title);
        appFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Display the window centered on the screen
        Dimension d = appFrame.getToolkit().getScreenSize();
        appFrame.setLocation((d.width / 2) - (appFrame.getWidth() / 2), (appFrame.getHeight() / 2));

        CarRentalClientGui gui = new CarRentalClientGui(searchModel, reserveModel);
        gui.appFrame = appFrame;
        appFrame.setContentPane(gui);
        appFrame.pack();

        appFrame.setVisible(true);
        appFrame.toFront();
    }

    public static void openApp(final CarSearchModel searchModel, final CarReserveModel reserveModel) {
       javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(searchModel, reserveModel);
            }
        });
    }

    public static void main(String[] args) {
		openApp(null, null);
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(cmdFind)) {
			if (search != null) {
				search.search((String) cUser.getSelectedItem()
								, tPickupDate.getText()
								, tReturnDate.getText());
				ctm.setData(search.getCars());
			}
			cardlist.show(content, SELECT);

		} else if (e.getSource().equals(cmdFindCancel) || e.getSource().equals(cmdClose)) {
			this.appFrame.dispose();

		} else if (e.getSource().equals(cmdSelect)) {
			int pos = selectTable.getSelectedRow();
			if (pos > -1) {
				if (search != null) {
					RESStatusType resStatus = reserve.reserveCar(search.getCustomer()
							, search.getCars().get(pos)
							, tPickupDate.getText()
							, tReturnDate.getText());
					ConfirmationType confirm = reserve.getConfirmation(resStatus
							, search.getCustomer()
							, search.getCars().get(pos)
							, tPickupDate.getText()
							, tReturnDate.getText());

					RESCarType car = confirm.getCar();
					CustomerDetailsType customer = confirm.getCustomer();

					lStatus.setText(confirm.getDescription());
					tReservationId.setText(confirm.getReservationId());
					tName.setText(customer.getName());
					tEMail.setText(customer.getEmail());
					tCity.setText(customer.getCity());
					tStatus.setText(customer.getStatus().name());
					tBrand.setText(car.getBrand());
					tModel.setText(car.getDesignModel());
					tPickup.setText(confirm.getFromDate());
					tReturn.setText(confirm.getToDate());
					tDaily.setText(car.getRateDay());
					tWeekEnd.setText(car.getRateWeekend());
					tCredits.setText(confirm.getCreditPoints().toString());
				}
				cardlist.show(content, CONFIRM);
			}

		} else if (e.getSource().equals(cmdSelectBack)) {
			cardlist.show(content, FIND);
		}
	}

}
