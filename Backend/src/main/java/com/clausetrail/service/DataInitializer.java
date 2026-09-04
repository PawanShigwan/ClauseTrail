package com.clausetrail.service;

import com.clausetrail.model.*;
import com.clausetrail.repository.AuditLogRepository;
import com.clausetrail.repository.ContractRepository;
import com.clausetrail.repository.ContractVersionRepository;
import com.clausetrail.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final ContractVersionRepository contractVersionRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ContractRepository contractRepository, ContractVersionRepository contractVersionRepository, AuditLogRepository auditLogRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.contractVersionRepository = contractVersionRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Checking and initializing ClauseTrail seed dataset...");

        // 1. Seed Users if not present
        User admin = seedUser("admin@clausetrail.com", "Eleanor Vance", Role.ADMIN, "Legal Operations", "Head of Legal Operations & Admin");
        User editor = seedUser("editor@clausetrail.com", "Marcus Reed", Role.EDITOR, "Corporate Legal Counsel", "Senior Contract Editor");
        User reviewer = seedUser("reviewer@clausetrail.com", "Sarah Jenkins", Role.REVIEWER, "Executive Legal Board", "Managing Partner & Lead Approver");
        User viewer = seedUser("viewer@clausetrail.com", "David Kim", Role.VIEWER, "Procurement & Compliance", "Procurement Auditor");

        // 2. Seed Contracts if empty
        if (contractRepository.count() == 0) {
            log.info("Seeding realistic enterprise legal contracts with multi-version history...");
            seedEnterpriseContracts(admin, editor, reviewer, viewer);
        }

        log.info("ClauseTrail data initialization complete!");
    }

    private User seedUser(String email, String name, Role role, String department, String title) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = User.builder()
                    .name(name)
                    .email(email)
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role(role)
                    .department(department)
                    .title(title)
                    .active(true)
                    .createdAt(Instant.now().minus(30, ChronoUnit.DAYS))
                    .updatedAt(Instant.now())
                    .build();
            User saved = userRepository.save(user);
            log.info("Created seed user: {} ({}) with role {}", name, email, role);
            return saved;
        });
    }

    private void seedEnterpriseContracts(User admin, User editor, User reviewer, User viewer) {
        Instant now = Instant.now();

        // -------------------------------------------------------------
        // Contract 1: Enterprise SaaS Master Subscription Agreement (With PENDING_REVIEW v2)
        // -------------------------------------------------------------
        Contract c1 = Contract.builder()
                .title("Enterprise SaaS Master Subscription Agreement")
                .contractType("Software as a Service (SaaS)")
                .parties(List.of("Apex Cloud Solutions Inc.", "Global Financial Partners Ltd."))
                .description("Multi-year enterprise cloud subscription agreement covering SOC-2 compliant hosting, 99.95% SLA, and tier-1 support.")
                .tags(List.of("Cloud", "SaaS", "Enterprise", "High-Priority"))
                .status(ContractStatus.PENDING_REVIEW)
                .currentVersionNumber(1)
                .pendingVersionNumber(2)
                .effectiveDate("2026-09-01")
                .expirationDate("2029-08-31")
                .contractValue("$450,000 USD / Year")
                .createdBy(editor.toReference())
                .lastModifiedBy(editor.toReference())
                .createdAt(now.minus(14, ChronoUnit.DAYS))
                .updatedAt(now.minus(2, ChronoUnit.HOURS))
                .build();
        c1 = contractRepository.save(c1);

        List<Clause> c1_v1_clauses = List.of(
                Clause.builder().id("c1-1").orderIndex(1).clauseNumber("1.0").title("Scope of Subscription").content("Apex Cloud Solutions grants Customer a non-exclusive, non-transferable right to access and use the SaaS Platform in accordance with the Documentation and Service Tier specifications.").category("Scope").build(),
                Clause.builder().id("c1-2").orderIndex(2).clauseNumber("2.0").title("Service Level Agreement (SLA)").content("Provider warrants that the Platform shall maintain 99.90% monthly uptime, excluding scheduled maintenance windows announced at least 48 hours in advance.").category("Service Level").build(),
                Clause.builder().id("c1-3").orderIndex(3).clauseNumber("3.0").title("Data Protection & Privacy").content("Provider shall maintain ISO 27001 and SOC 2 Type II compliance. Customer data shall be encrypted in transit using TLS 1.3 and at rest using AES-256 encryption keys managed in a dedicated HSM.").category("Security").build(),
                Clause.builder().id("c1-4").orderIndex(4).clauseNumber("4.0").title("Limitation of Liability").content("Except for gross negligence or willful misconduct, either party's maximum aggregate liability arising under this Agreement shall not exceed twelve (12) months of fees paid.").category("Liability").build(),
                Clause.builder().id("c1-5").orderIndex(5).clauseNumber("5.0").title("Term & Termination").content("This Agreement commences on the Effective Date and shall continue for an initial term of 36 months, renewing automatically for successive 12-month periods unless notice is given 60 days prior.").category("Termination").build()
        );

        ContractVersion c1_v1 = ContractVersion.builder()
                .contractId(c1.getId())
                .versionNumber(1)
                .fullText(joinClauses(c1_v1_clauses))
                .clauses(c1_v1_clauses)
                .modifiedBy(editor.toReference())
                .modificationReason("Initial baseline contract agreed by procurement")
                .status(VersionStatus.APPROVED)
                .reviewedBy(reviewer.toReference())
                .reviewComments("Initial baseline verified and approved.")
                .reviewedAt(now.minus(13, ChronoUnit.DAYS))
                .createdAt(now.minus(14, ChronoUnit.DAYS))
                .build();
        c1_v1 = contractVersionRepository.save(c1_v1);
        c1.setCurrentVersionId(c1_v1.getId());

        // Version 2 (Pending Review: Updated SLA to 99.95% and revised liability cap to 24 months for data breaches)
        List<Clause> c1_v2_clauses = List.of(
                Clause.builder().id("c1-1").orderIndex(1).clauseNumber("1.0").title("Scope of Subscription").content("Apex Cloud Solutions grants Customer a non-exclusive, non-transferable right to access and use the SaaS Platform in accordance with the Documentation and Service Tier specifications.").category("Scope").build(),
                Clause.builder().id("c1-2").orderIndex(2).clauseNumber("2.0").title("Service Level Agreement (SLA)").content("Provider warrants that the Platform shall maintain 99.95% monthly uptime, excluding scheduled maintenance windows announced at least 72 hours in advance.").category("Service Level").build(),
                Clause.builder().id("c1-3").orderIndex(3).clauseNumber("3.0").title("Data Protection & Privacy").content("Provider shall maintain ISO 27001 and SOC 2 Type II compliance. Customer data shall be encrypted in transit using TLS 1.3 and at rest using AES-256 encryption keys managed in a dedicated HSM.").category("Security").build(),
                Clause.builder().id("c1-4").orderIndex(4).clauseNumber("4.0").title("Limitation of Liability").content("Except for breaches of Data Protection (Section 3.0), gross negligence, or willful misconduct, either party's maximum aggregate liability arising under this Agreement shall not exceed twenty-four (24) months of fees paid.").category("Liability").build(),
                Clause.builder().id("c1-5").orderIndex(5).clauseNumber("5.0").title("Term & Termination").content("This Agreement commences on the Effective Date and shall continue for an initial term of 36 months, renewing automatically for successive 12-month periods unless notice is given 60 days prior.").category("Termination").build()
        );

        List<ClauseChange> c1_changes = List.of(
                ClauseChange.builder().clauseId("c1-2").clauseNumber("2.0").clauseTitle("Service Level Agreement (SLA)").previousText("Provider warrants that the Platform shall maintain 99.90% monthly uptime, excluding scheduled maintenance windows announced at least 48 hours in advance.").modifiedText("Provider warrants that the Platform shall maintain 99.95% monthly uptime, excluding scheduled maintenance windows announced at least 72 hours in advance.").changeType(ChangeType.MODIFIED).build(),
                ClauseChange.builder().clauseId("c1-4").clauseNumber("4.0").clauseTitle("Limitation of Liability").previousText("Except for gross negligence or willful misconduct, either party's maximum aggregate liability arising under this Agreement shall not exceed twelve (12) months of fees paid.").modifiedText("Except for breaches of Data Protection (Section 3.0), gross negligence, or willful misconduct, either party's maximum aggregate liability arising under this Agreement shall not exceed twenty-four (24) months of fees paid.").changeType(ChangeType.MODIFIED).build()
        );

        ContractVersion c1_v2 = ContractVersion.builder()
                .contractId(c1.getId())
                .versionNumber(2)
                .fullText(joinClauses(c1_v2_clauses))
                .clauses(c1_v2_clauses)
                .clauseChanges(c1_changes)
                .modifiedBy(editor.toReference())
                .modificationReason("Legal renegotiation: Uptime SLA increased to 99.95% and liability cap extended to 24 months for data protection incidents per enterprise risk committee.")
                .status(VersionStatus.PENDING_REVIEW)
                .parentVersionId(c1_v1.getId())
                .parentVersionNumber(1)
                .createdAt(now.minus(2, ChronoUnit.HOURS))
                .build();
        c1_v2 = contractVersionRepository.save(c1_v2);
        c1.setPendingVersionId(c1_v2.getId());
        contractRepository.save(c1);

        // Audit logs for Contract 1
        createAudit(c1, editor, AuditAction.UPLOAD_CONTRACT, "Initial contract creation with 5 clauses (v1.0)", 1, now.minus(14, ChronoUnit.DAYS));
        createAudit(c1, reviewer, AuditAction.APPROVE_MODIFICATION, "Baseline v1.0 approved and signed", 1, now.minus(13, ChronoUnit.DAYS));
        createAudit(c1, editor, AuditAction.MODIFY_CLAUSE, "Edited Clause 2.0 (SLA) and Clause 4.0 (Liability)", 2, now.minus(2, ChronoUnit.HOURS));
        createAudit(c1, editor, AuditAction.SUBMIT_FOR_REVIEW, "Submitted v2.0 for approval with 2 clause revisions", 2, now.minus(2, ChronoUnit.HOURS));

        // -------------------------------------------------------------
        // Contract 2: Mutual Non-Disclosure Agreement (Approved v2.0)
        // -------------------------------------------------------------
        Contract c2 = Contract.builder()
                .title("Mutual Non-Disclosure Agreement (NDA)")
                .contractType("Non-Disclosure Agreement (NDA)")
                .parties(List.of("CyberShield Security Corp", "Quantum Innovations LLC"))
                .description("Standard bilateral confidentiality agreement covering proprietary AI model architectures and source code disclosures.")
                .tags(List.of("Confidentiality", "Bilateral", "IP Protection"))
                .status(ContractStatus.APPROVED)
                .currentVersionNumber(2)
                .effectiveDate("2026-08-01")
                .expirationDate("2031-08-01")
                .contractValue("N/A")
                .createdBy(editor.toReference())
                .lastModifiedBy(reviewer.toReference())
                .createdAt(now.minus(20, ChronoUnit.DAYS))
                .updatedAt(now.minus(5, ChronoUnit.DAYS))
                .build();
        c2 = contractRepository.save(c2);

        List<Clause> c2_v1_clauses = List.of(
                Clause.builder().id("c2-1").orderIndex(1).clauseNumber("1.0").title("Definition of Confidential Information").content("Confidential Information includes all non-public technical, financial, and business data disclosed by either party marked as proprietary.").category("Definitions").build(),
                Clause.builder().id("c2-2").orderIndex(2).clauseNumber("2.0").title("Obligations of Receiving Party").content("The Receiving Party agrees to protect disclosed Confidential Information using at least the same degree of care as its own proprietary materials, but not less than reasonable care.").category("Obligations").build(),
                Clause.builder().id("c2-3").orderIndex(3).clauseNumber("3.0").title("Term of Confidentiality").content("Confidentiality obligations under this Agreement shall survive for a period of two (2) years following initial disclosure.").category("Term").build()
        );

        ContractVersion c2_v1 = ContractVersion.builder()
                .contractId(c2.getId())
                .versionNumber(1)
                .fullText(joinClauses(c2_v1_clauses))
                .clauses(c2_v1_clauses)
                .modifiedBy(editor.toReference())
                .modificationReason("Initial draft standard mutual NDA")
                .status(VersionStatus.APPROVED)
                .reviewedBy(reviewer.toReference())
                .reviewedAt(now.minus(19, ChronoUnit.DAYS))
                .createdAt(now.minus(20, ChronoUnit.DAYS))
                .build();
        c2_v1 = contractVersionRepository.save(c2_v1);

        List<Clause> c2_v2_clauses = List.of(
                Clause.builder().id("c2-1").orderIndex(1).clauseNumber("1.0").title("Definition of Confidential Information").content("Confidential Information includes all non-public technical, source code, AI weights, financial, and business data disclosed by either party.").category("Definitions").build(),
                Clause.builder().id("c2-2").orderIndex(2).clauseNumber("2.0").title("Obligations of Receiving Party").content("The Receiving Party agrees to protect disclosed Confidential Information using at least the same degree of care as its own proprietary materials, but not less than reasonable care.").category("Obligations").build(),
                Clause.builder().id("c2-3").orderIndex(3).clauseNumber("3.0").title("Term of Confidentiality").content("Confidentiality obligations under this Agreement shall survive for a period of five (5) years following initial disclosure, and in perpetuity for trade secrets.").category("Term").build()
        );

        List<ClauseChange> c2_changes = List.of(
                ClauseChange.builder().clauseId("c2-1").clauseNumber("1.0").clauseTitle("Definition of Confidential Information").previousText(c2_v1_clauses.get(0).getContent()).modifiedText(c2_v2_clauses.get(0).getContent()).changeType(ChangeType.MODIFIED).build(),
                ClauseChange.builder().clauseId("c2-3").clauseNumber("3.0").clauseTitle("Term of Confidentiality").previousText(c2_v1_clauses.get(2).getContent()).modifiedText(c2_v2_clauses.get(2).getContent()).changeType(ChangeType.MODIFIED).build()
        );

        ContractVersion c2_v2 = ContractVersion.builder()
                .contractId(c2.getId())
                .versionNumber(2)
                .fullText(joinClauses(c2_v2_clauses))
                .clauses(c2_v2_clauses)
                .clauseChanges(c2_changes)
                .modifiedBy(editor.toReference())
                .modificationReason("Extended confidentiality duration to 5 years and included explicit protection for AI neural network weights.")
                .status(VersionStatus.APPROVED)
                .reviewedBy(reviewer.toReference())
                .reviewComments("Approved. Crucial for IP protection before partner demos.")
                .reviewedAt(now.minus(5, ChronoUnit.DAYS))
                .parentVersionId(c2_v1.getId())
                .parentVersionNumber(1)
                .createdAt(now.minus(6, ChronoUnit.DAYS))
                .build();
        c2_v2 = contractVersionRepository.save(c2_v2);

        c2.setCurrentVersionId(c2_v2.getId());
        contractRepository.save(c2);

        createAudit(c2, editor, AuditAction.UPLOAD_CONTRACT, "Created NDA v1.0", 1, now.minus(20, ChronoUnit.DAYS));
        createAudit(c2, editor, AuditAction.SUBMIT_FOR_REVIEW, "Submitted v2.0 extending term to 5 years", 2, now.minus(6, ChronoUnit.DAYS));
        createAudit(c2, reviewer, AuditAction.APPROVE_MODIFICATION, "Approved v2.0 modifications", 2, now.minus(5, ChronoUnit.DAYS));

        // -------------------------------------------------------------
        // Contract 3: Strategic Vendor & Software Development Agreement
        // -------------------------------------------------------------
        Contract c3 = Contract.builder()
                .title("Strategic Vendor & Software Development Agreement")
                .contractType("Master Services Agreement (MSA)")
                .parties(List.of("AlphaTech Logistics", "NextGen Systems Inc."))
                .description("Outsourced bespoke software development and platform engineering agreement.")
                .tags(List.of("Engineering", "Outsourcing", "Deliverables"))
                .status(ContractStatus.APPROVED)
                .currentVersionNumber(1)
                .effectiveDate("2026-10-01")
                .expirationDate("2027-09-30")
                .contractValue("$780,000 USD")
                .createdBy(editor.toReference())
                .lastModifiedBy(editor.toReference())
                .createdAt(now.minus(10, ChronoUnit.DAYS))
                .updatedAt(now.minus(10, ChronoUnit.DAYS))
                .build();
        c3 = contractRepository.save(c3);

        List<Clause> c3_clauses = List.of(
                Clause.builder().id("c3-1").orderIndex(1).clauseNumber("1.0").title("Services & Deliverables").content("Vendor shall deliver software modules described in Statement of Work (SOW) #1 within 180 business days.").category("Deliverables").build(),
                Clause.builder().id("c3-2").orderIndex(2).clauseNumber("2.0").title("Intellectual Property Assignment").content("All Work Product, custom code, and documentation developed by Vendor under this Agreement shall belong exclusively to AlphaTech Logistics upon full payment.").category("IP Assignment").build(),
                Clause.builder().id("c3-3").orderIndex(3).clauseNumber("3.0").title("Payment Milestones & Invoicing").content("Payments shall be released net 30 upon formal User Acceptance Testing (UAT) milestone sign-offs.").category("Financial").build(),
                Clause.builder().id("c3-4").orderIndex(4).clauseNumber("4.0").title("Warranty & Bug Remediation").content("Vendor provides a 90-day defect remediation warranty following final production deployment at zero additional charge.").category("Warranty").build()
        );

        ContractVersion c3_v1 = ContractVersion.builder()
                .contractId(c3.getId())
                .versionNumber(1)
                .fullText(joinClauses(c3_clauses))
                .clauses(c3_clauses)
                .modifiedBy(editor.toReference())
                .modificationReason("Initial standard engineering MSA baseline")
                .status(VersionStatus.APPROVED)
                .reviewedBy(reviewer.toReference())
                .reviewComments("Approved by Engineering VP & Legal")
                .reviewedAt(now.minus(9, ChronoUnit.DAYS))
                .createdAt(now.minus(10, ChronoUnit.DAYS))
                .build();
        c3_v1 = contractVersionRepository.save(c3_v1);
        c3.setCurrentVersionId(c3_v1.getId());
        contractRepository.save(c3);

        createAudit(c3, editor, AuditAction.UPLOAD_CONTRACT, "Uploaded Software Development MSA v1.0", 1, now.minus(10, ChronoUnit.DAYS));
        createAudit(c3, reviewer, AuditAction.APPROVE_MODIFICATION, "Approved MSA v1.0", 1, now.minus(9, ChronoUnit.DAYS));

        // -------------------------------------------------------------
        // Contract 4: Senior Executive Employment Agreement (With REJECTED v2)
        // -------------------------------------------------------------
        Contract c4 = Contract.builder()
                .title("Senior Executive Employment & IP Assignment Agreement")
                .contractType("Employment Agreement")
                .parties(List.of("OmniCorp Global Inc.", "Dr. Robert Vance"))
                .description("Chief Technology Officer employment terms, severance package, non-compete covenant, and invention assignment.")
                .tags(List.of("Employment", "Executive", "C-Suite", "Confidential"))
                .status(ContractStatus.APPROVED)
                .currentVersionNumber(1)
                .effectiveDate("2026-07-01")
                .expirationDate("Indefinite")
                .contractValue("$380,000 Base + Equity")
                .createdBy(admin.toReference())
                .lastModifiedBy(reviewer.toReference())
                .createdAt(now.minus(25, ChronoUnit.DAYS))
                .updatedAt(now.minus(1, ChronoUnit.DAYS))
                .build();
        c4 = contractRepository.save(c4);

        List<Clause> c4_v1_clauses = List.of(
                Clause.builder().id("c4-1").orderIndex(1).clauseNumber("1.0").title("Position & Responsibilities").content("Executive shall serve as Chief Technology Officer reporting directly to the Chief Executive Officer and Board of Directors.").category("Role").build(),
                Clause.builder().id("c4-2").orderIndex(2).clauseNumber("2.0").title("Compensation & Equity Grants").content("Base annual salary of $380,000 USD paid bi-weekly, with an initial incentive stock option grant of 150,000 shares vesting over 4 years.").category("Compensation").build(),
                Clause.builder().id("c4-3").orderIndex(3).clauseNumber("3.0").title("Non-Compete & Non-Solicitation").content("Executive shall not engage in competing ventures within North America for a period of twelve (12) months following separation.").category("Restrictive Covenants").build()
        );

        ContractVersion c4_v1 = ContractVersion.builder()
                .contractId(c4.getId())
                .versionNumber(1)
                .fullText(joinClauses(c4_v1_clauses))
                .clauses(c4_v1_clauses)
                .modifiedBy(admin.toReference())
                .modificationReason("Executive offer package agreement baseline")
                .status(VersionStatus.APPROVED)
                .reviewedBy(reviewer.toReference())
                .reviewedAt(now.minus(24, ChronoUnit.DAYS))
                .createdAt(now.minus(25, ChronoUnit.DAYS))
                .build();
        c4_v1 = contractVersionRepository.save(c4_v1);
        c4.setCurrentVersionId(c4_v1.getId());

        // Version 2 was rejected (attempt to delete non-compete clause)
        List<Clause> c4_v2_clauses = List.of(
                Clause.builder().id("c4-1").orderIndex(1).clauseNumber("1.0").title("Position & Responsibilities").content("Executive shall serve as Chief Technology Officer reporting directly to the Chief Executive Officer and Board of Directors.").category("Role").build(),
                Clause.builder().id("c4-2").orderIndex(2).clauseNumber("2.0").title("Compensation & Equity Grants").content("Base annual salary of $420,000 USD paid bi-weekly, with an initial incentive stock option grant of 200,000 shares vesting over 4 years.").category("Compensation").build()
        );

        List<ClauseChange> c4_changes = List.of(
                ClauseChange.builder().clauseId("c4-2").clauseNumber("2.0").clauseTitle("Compensation & Equity Grants").previousText(c4_v1_clauses.get(1).getContent()).modifiedText(c4_v2_clauses.get(1).getContent()).changeType(ChangeType.MODIFIED).build(),
                ClauseChange.builder().clauseId("c4-3").clauseNumber("3.0").clauseTitle("Non-Compete & Non-Solicitation").previousText(c4_v1_clauses.get(2).getContent()).modifiedText("").changeType(ChangeType.DELETED).build()
        );

        ContractVersion c4_v2 = ContractVersion.builder()
                .contractId(c4.getId())
                .versionNumber(2)
                .fullText(joinClauses(c4_v2_clauses))
                .clauses(c4_v2_clauses)
                .clauseChanges(c4_changes)
                .modifiedBy(editor.toReference())
                .modificationReason("Requested by candidate counsel: Remove 12-month non-compete clause and increase base to $420k.")
                .status(VersionStatus.REJECTED)
                .reviewedBy(reviewer.toReference())
                .reviewComments("Rejected by Compensation Committee. Non-compete covenant is mandatory for C-level officers.")
                .reviewedAt(now.minus(1, ChronoUnit.DAYS))
                .parentVersionId(c4_v1.getId())
                .parentVersionNumber(1)
                .createdAt(now.minus(2, ChronoUnit.DAYS))
                .build();
        c4_v2 = contractVersionRepository.save(c4_v2);
        contractRepository.save(c4);

        createAudit(c4, admin, AuditAction.UPLOAD_CONTRACT, "Created Executive Contract v1.0", 1, now.minus(25, ChronoUnit.DAYS));
        createAudit(c4, editor, AuditAction.SUBMIT_FOR_REVIEW, "Submitted v2.0 proposing removal of non-compete clause", 2, now.minus(2, ChronoUnit.DAYS));
        createAudit(c4, reviewer, AuditAction.REJECT_MODIFICATION, "Rejected v2.0: Non-compete covenant mandatory", 2, now.minus(1, ChronoUnit.DAYS));
    }

    private void createAudit(Contract contract, User user, AuditAction action, String details, Integer versionNumber, Instant timestamp) {
        AuditLog log = AuditLog.builder()
                .contractId(contract.getId())
                .contractTitle(contract.getTitle())
                .userId(user.getId())
                .userName(user.getName())
                .userRole(user.getRole())
                .action(action)
                .details(details)
                .versionNumber(versionNumber)
                .ipAddress("127.0.0.1")
                .timestamp(timestamp)
                .build();
        auditLogRepository.save(log);
    }

    private String joinClauses(List<Clause> clauses) {
        StringBuilder sb = new StringBuilder();
        for (Clause c : clauses) {
            sb.append(c.getClauseNumber()).append(" ").append(c.getTitle()).append("\n")
                    .append(c.getContent()).append("\n\n");
        }
        return sb.toString().trim();
    }
}
