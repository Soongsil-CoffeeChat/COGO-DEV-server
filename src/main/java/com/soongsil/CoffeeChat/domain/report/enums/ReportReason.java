package com.soongsil.CoffeeChat.domain.report.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "신고 사유 코드")
public enum ReportReason {
    @Schema(description = "목적이 다른 것 같아요")
    PURPOSE_MISMATCH,
    @Schema(description = "멘토링 중에 분쟁이 발생했어요")
    CONFLICT,
    @Schema(description = "멘토의 소속 및 인적사항이 거짓인 것 같아요")
    FALSE_INFO,
    @Schema(description = "기타 부적절한 행위가 있었어요")
    OTHER
}
