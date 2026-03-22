package org.example.dto.enterprise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseExport {
    private byte[] content;
    private String fileName;
    private String contentType;
}
