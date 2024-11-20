package com.sharespace.sharespace_server.place.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sharespace.sharespace_server.global.enums.Category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class PlaceUpdateRequest {
	@NotNull(message = "Title을 입력해주세요")
	@Size(max = 50, message = "제목은 50자 이내로 작성해주세요")
	private String title;
	@NotNull(message = "Category를 선택해주세요")
	private Category category;
	@NotNull(message = "최대 보관 기간을 입력해주세요")
	private String period;
	@NotNull(message = "위치 정보를 입력해주세요")
	private String location;
	private List<MultipartFile> newImageUrl;
	private List<String> deleteImageUrl;
	@Size(max = 100, message = "비고는 100자 이내로 작성해주세요")
	private String description;
}
