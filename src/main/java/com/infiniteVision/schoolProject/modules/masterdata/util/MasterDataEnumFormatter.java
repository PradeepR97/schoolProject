package com.infiniteVision.schoolProject.modules.masterdata.util;

import com.infiniteVision.schoolProject.modules.masterdata.dto.MasterDataOptionDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds {@link MasterDataOptionDTO} lists from Java enums for master data APIs.
 */
public final class MasterDataEnumFormatter {

    private MasterDataEnumFormatter() {
    }

    /**
     * Maps enum constants to options with sequential {@code id} and {@code value} = {@link Enum#name()}.
     */
    public static <E extends Enum<E>> List<MasterDataOptionDTO> fromEnum(E[] values) {
        List<MasterDataOptionDTO> options = new ArrayList<>(values.length);
        long id = 1L;
        for (E value : values) {
            options.add(MasterDataOptionDTO.builder()
                    .id(id++)
                    .label(toDisplayLabel(value.name()))
                    .value(value.name())
                    .build());
        }
        return options;
    }

    /**
     * Converts {@code STAFF_WARD} to {@code Staff ward}.
     */
    public static String toDisplayLabel(String enumName) {
        String[] parts = enumName.toLowerCase().split("_");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) {
                continue;
            }
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }
}
