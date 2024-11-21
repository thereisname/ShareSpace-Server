package com.sharespace.sharespace_server.matching.entity;

import static com.sharespace.sharespace_server.global.enums.Status.*;

import java.time.LocalDateTime;

import com.sharespace.sharespace_server.global.enums.Status;
import com.sharespace.sharespace_server.global.exception.CustomRuntimeException;
import com.sharespace.sharespace_server.global.exception.error.MatchingException;
import com.sharespace.sharespace_server.global.utils.LocationTransform;
import com.sharespace.sharespace_server.place.entity.Place;
import com.sharespace.sharespace_server.product.entity.Product;
import com.sharespace.sharespace_server.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "matching")
public class Matching {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;


	@Column(columnDefinition = "TEXT")
	private String image;

	@Enumerated(EnumType.STRING)
	private Status status;

	private boolean hostCompleted;

	private boolean guestCompleted;

	private Integer distance;

	private LocalDateTime startDate;

	private LocalDateTime expiryDate;

	public static Matching createNotSelectedPlace(Product product) {
		return Matching.builder()
			.place(null)
			.distance(null)
			.product(product)
			.status(UNASSIGNED)
			.startDate(LocalDateTime.now())
			.expiryDate(null)
			.build();
	}
	public static Matching createSelectedPlace(Product product, Place place) {
		Integer distance = calculateDistance(product.getUser(), place.getUser());
		Matching matching = Matching.builder()
			.product(product)
			.place(place)
			.status(REQUESTED)
			.distance(distance)
			.startDate(LocalDateTime.now())
			.expiryDate(LocalDateTime.now().plusDays(product.getPeriod()))
			.build();
		product.setIsPlaced(true);
		return matching;
	}
	public void updatePlace(Place place) {
		this.place = place;
		this.distance = calculateDistance(this.product.getUser(), place.getUser());
		this.status = REQUESTED;
		this.expiryDate = LocalDateTime.now().plusDays(this.product.getPeriod());
	}

	public static Integer calculateDistance(User guest, User host) {
		return LocationTransform.calculateDistance(guest.getLatitude(), guest.getLongitude(), host.getLatitude(), host.getLongitude());
	}

	public void completeStorage(User user) {
		if (user.getRole().getValue().equals("GUEST")) {
			completeGuestStorage();
		} else if (user.getRole().getValue().equals("HOST")) {
			completeHostStorage();
		}

		if (this.isGuestCompleted() && this.isHostCompleted()) {
			this.setStatus(Status.COMPLETED);
		}
	}

	private void completeGuestStorage() {
		if (this.isGuestCompleted()) {
			throw new CustomRuntimeException(MatchingException.GUEST_ALREADY_COMPLETED_KEEPING);
		}
		this.setGuestCompleted(true);
	}

	private void completeHostStorage() {
		if (this.isHostCompleted()) {
			throw new CustomRuntimeException(MatchingException.HOST_ALREADY_COMPLETED_KEEPING);
		}
		this.setHostCompleted(true);
	}

	public void cancel(User user) {
		if (!this.getStatus().equals(Status.PENDING)) {
			throw new CustomRuntimeException(MatchingException.REQUEST_CANCELLATION_NOT_ALLOWED);
		}
		// 물품 배정 상태 변경
		if (!this.status.equals(UNASSIGNED)) {
			this.product.unassign();
			this.setStatus(UNASSIGNED);
			this.setPlace(null);
		}
	}

	public void confirmStorageByGuest() {
		if (!this.getStatus().equals(Status.PENDING)) {
			throw new CustomRuntimeException(MatchingException.INCORRECT_STATUS_CONFIRM_REQUEST_GUEST);
		}
		this.setStatus(Status.STORED);
	}
}
