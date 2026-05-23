package com.infiniteVision.schoolProject.modules.student.dto.response;

import com.infiniteVision.schoolProject.modules.student.enums.DocumentVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDocumentsResponseDTO {

    private Long documentRecordId;
    private String profilePhotoUrl;
    private String aadharNo;
    private String aadharFileUrl;
    private DocumentVerificationStatus verificationStatus;
}
