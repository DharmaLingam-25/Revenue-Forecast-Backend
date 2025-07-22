package com.clt.ops.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ASSOCIATE_DATA")

public class AssociateEntity {

    @Id
    @Column(name = "CTS_ID")
    private String ctsId;

    @Column(name = "EXTERNAL_ID")
    private String externalId;

    @Column(name = "CONTRACTOR_NAME")
    private String contractorName;

    @Column(name = "CONTRACTOR_EMAIL")
    private String contractorEmail;

    @Column(name = "ESA_PROJECT_ID")
    private String esaProjectId;

    @Column(name = "PROJECT_DESCRIPTION")
    private String projectDescription;

    @Column(name = "PROJECT_TYPE")
    private String projectType;

    @Column(name = "ESA_PM")
    private String esaPm;

    @Column(name = "SL")
    private String sl;
    

    @Column(name = "ESA_ID")
    private String esaId;
    

    @Column(name = "ACC_ID")
    private String accId;
    

    @Column(name = "ACC_NAME")
    private String accName;
    
}
