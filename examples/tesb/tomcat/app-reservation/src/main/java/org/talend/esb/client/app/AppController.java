package org.talend.esb.client.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.talend.esb.client.app.model.SearchRequestParameters;
import org.talend.esb.client.app.model.SelectedCarParameters;
import org.talend.esb.client.app.validation.DateFormatValidator;
import org.talend.esb.client.model.CarReserveModel;
import org.talend.esb.client.model.CarSearchModel;
import org.talend.services.crm.types.CustomerDetailsType;
import org.talend.services.reservation.types.ConfirmationType;
import org.talend.services.reservation.types.RESCarType;
import org.talend.services.reservation.types.RESStatucCodeType;
import org.talend.services.reservation.types.RESStatusType;

@Controller
@SessionAttributes("inputData")
public class AppController {

	private static final Logger LOG = LogManager.getLogger(AppController.class);

	private static final long ADAY = 24 * 60 * 61 * 1000;

	private static final String[] KNOWS_CUSTOMERS = { "aebert", "jdoe",
			"bbrindle", "rlambert" };

	@Resource
	private CarSearchModel searchModel;

	@Resource
	private CarReserveModel reserveModel;

	@RequestMapping("/")
	public String index(Model model, HttpSession session) {
		Stages stage = getSessionAttribute(session, Stages.class);

		if (stage == null) {
			stage = Stages.DATA_INPUT;
			setSessionAttribute(session, Stages.class, stage);
		}

		if (stage.equals(Stages.DATA_INPUT)) {
			model.addAttribute("knownCustomers", KNOWS_CUSTOMERS);
		}

		if (stage.equals(Stages.CAR_LIST)) {
			model.addAttribute("cars", searchModel.getCars());
			model.addAttribute("command", new SelectedCarParameters());
		}

		if (stage.equals(Stages.RESERVATION_INFO)) {
			model.addAttribute("confirm",
					getSessionAttribute(session, ConfirmationType.class));
			model.addAttribute("resStatus",
					getSessionAttribute(session, RESStatucCodeType.class));
		}

		return stage.getView();
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search() {
		return redirectToMainPage();
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(
			@Valid @ModelAttribute("inputData") SearchRequestParameters req,
			BindingResult bindResult, Model model, HttpSession session) {
		if (session.isNew()) {
			return redirectToMainPage();
		}

		Stages stage = getSessionAttribute(session, Stages.class);

		if (stage != null && stage.equals(Stages.DATA_INPUT)) {
			StringBuilder sb = new StringBuilder();

			sb.append("Received a request to search: customerName(")
					.append(req.getCustomerName()).append("), pickupDate(")
					.append(req.getPickupDate()).append("), returnDate(")
					.append(req.getReturnDate()).append(")");

			LOG.info(sb);

			if (bindResult.hasErrors()) {
				model.addAttribute("knownCustomers", KNOWS_CUSTOMERS);
				return stage.getView();
			}

			try {
				searchModel.search(req.getCustomerName(), req.getPickupDate(),
						req.getReturnDate());
			} catch (Exception e) {
				model.addAttribute("excptMessage", e);
				return "error";
			}

			setSessionAttribute(session, SearchRequestParameters.class,
					new SearchRequestParameters(req));

			return setStageAndRedirect(session, Stages.CAR_LIST);
		}

		return redirectToMainPage();
	}

	@RequestMapping(value = "/reserve", method = RequestMethod.GET)
	public String reserve() {
		return redirectToMainPage();
	}

	@RequestMapping(value = "/reserve", method = RequestMethod.POST)
	public String reserve(
			@RequestParam(value = "carIndex", required = false) Integer carIndex,
			Model model, HttpSession session) {
		if (session.isNew()) {
			return redirectToMainPage();
		}

		if (carIndex != null && carIndex >= 0) {
			List<RESCarType> cars = searchModel.getCars();
			if (carIndex < cars.size()) {
				CustomerDetailsType customerDetails = searchModel.getCustomer();
				RESCarType selectedCar = cars.get(carIndex);
				SearchRequestParameters requestParams = getSessionAttribute(
						session, SearchRequestParameters.class);

				RESStatusType resStatus = null;

				ConfirmationType confirm = null;

				try {
					resStatus = reserveModel.reserveCar(customerDetails,
							selectedCar, requestParams.getPickupDate(),
							requestParams.getReturnDate());

					confirm = reserveModel.getConfirmation(resStatus,
							customerDetails, selectedCar,
							requestParams.getPickupDate(),
							requestParams.getReturnDate());
				} catch (Exception e) {
					model.addAttribute("excptMessage", e);
					return "error";
				}

				setSessionAttribute(session, ConfirmationType.class, confirm);

				RESStatucCodeType code = confirm.getDescription().contains(
						"failed") ? RESStatucCodeType.FAILED
						: RESStatucCodeType.OK;
				setSessionAttribute(session, RESStatucCodeType.class, code);

				setSessionAttribute(session, Stages.class,
						Stages.RESERVATION_INFO);
			}
		}

		return redirectToMainPage();
	}

	@RequestMapping(value = "/back", method = RequestMethod.GET)
	public String back() {
		return redirectToMainPage();
	}

	@RequestMapping(value = "/back", method = RequestMethod.POST)
	public String back(HttpSession session) {
		if (session.isNew()) {
			return redirectToMainPage();
		}

		Stages stage = getSessionAttribute(session, Stages.class);

		if (stage != null && stage.equals(Stages.CAR_LIST)) {
			setSessionAttribute(session, Stages.class, Stages.DATA_INPUT);
		}

		return redirectToMainPage();
	}

	@RequestMapping(value = "/done", method = RequestMethod.GET)
	public String done() {
		return redirectToMainPage();
	}

	@RequestMapping(value = "/done", method = RequestMethod.POST)
	public String done(HttpSession session, SessionStatus sessionStatus) {

		session.removeAttribute(Stages.class.getName());
		session.removeAttribute(SearchRequestParameters.class.getName());
		session.removeAttribute(ConfirmationType.class.getName());

		sessionStatus.setComplete();

		return redirectToMainPage();
	}

	@ModelAttribute("inputData")
	public SearchRequestParameters createRequestParameters() {
		SimpleDateFormat sdf = new SimpleDateFormat(
				DateFormatValidator.DEFAULT_FORMAT);
		SearchRequestParameters params = new SearchRequestParameters();
		params.setCustomerName("aebert");
		params.setPickupDate(sdf.format(new Date()));
		params.setReturnDate(sdf.format(new Date(System.currentTimeMillis()
				+ ADAY)));
		return params;
	}

	private String setStageAndRedirect(HttpSession session, Stages stage) {
		if (stage == null) {
			throw new NullPointerException();
		}
		setSessionAttribute(session, Stages.class, stage);
		return redirectToMainPage();
	}

	private String redirectToMainPage() {
		return "redirect:/";
	}

	protected <T> T getSessionAttribute(HttpSession session, Class<T> clz) {
		return clz.cast(session.getAttribute(clz.getName()));
	}

	private <T> void setSessionAttribute(HttpSession session, Class<T> clz,
			T value) {
		if (value == null) {
			throw new NullPointerException();
		}
		session.setAttribute(clz.getName(), value);
	}
}
