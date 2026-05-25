package com.infiniteVision.schoolProject.modules.excelupload.template;

/**
 * Two sample student rows for bulk admission Excel testing.
 */
public final class StudentAdmissionExcelSampleData {

    private StudentAdmissionExcelSampleData() {
    }

    /** Header row is built from {@link com.infiniteVision.schoolProject.modules.excelupload.parser.StudentAdmissionExcelColumn}. */
    public static final Object[][] SAMPLE_ROWS = {
            {
                "ADM-2026-003",
                "Arun",
                "Kumar",
                "2012-05-15",
                "ENGLISH",
                "MALE",
                1,
                1,
                "123456789012",
                "EMIS123456",
                "RC987654",
                "Indian",
                "12, Main Street, Chennai",
                "O_POSITIVE",
                "HINDU",
                "OC",
                450000,
                "ACTIVE",
                "PENDING",
                "Ravi Kumar",
                "9876543210",
                "ravi.kumar@example.com",
                "Engineer",
                600000,
                "Priya Kumar",
                "9876543211",
                "priya.kumar@example.com",
                "Teacher",
                350000,
                "Suresh Kumar",
                "9876543212",
                "suresh.kumar@example.com",
                "Business",
                "Uncle",
                "FATHER",
                "https://cdn.example.com/students/arun-photo.jpg",
                "123456789012"
            },
            {
                "ADM-2026-004",
                "Priya",
                "Sharma",
                "2011-08-20",
                "TAMIL",
                "FEMALE",
                1,
                1,
                "987654321098",
                "EMIS654321",
                "RC123456",
                "Indian",
                "45, Anna Salai, Coimbatore",
                "A_POSITIVE",
                "CHRISTIAN",
                "BC",
                320000,
                "ACTIVE",
                "PENDING",
                "Rajesh Sharma",
                "9123456780",
                "rajesh.sharma@example.com",
                "Accountant",
                480000,
                "Lakshmi Sharma",
                "9123456781",
                "lakshmi.sharma@example.com",
                "Nurse",
                420000,
                "",
                "",
                "",
                "",
                "",
                "MOTHER",
                "https://cdn.example.com/students/priya-photo.jpg",
                "987654321098"
            }
    };
}
