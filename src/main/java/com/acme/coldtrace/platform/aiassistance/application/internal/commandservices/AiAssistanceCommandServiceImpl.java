package com.acme.coldtrace.platform.aiassistance.application.internal.commandservices;

import com.acme.coldtrace.platform.aiassistance.application.commandservices.AiAssistanceCommandService;
import com.acme.coldtrace.platform.aiassistance.application.commandservices.AiAssistanceFailure;
import com.acme.coldtrace.platform.aiassistance.application.model.AiGeneratedResponse;
import com.acme.coldtrace.platform.aiassistance.application.model.AiStructuredPrompt;
import com.acme.coldtrace.platform.aiassistance.application.model.ComplianceSummaryDraft;
import com.acme.coldtrace.platform.aiassistance.application.model.DashboardInterpretationDraft;
import com.acme.coldtrace.platform.aiassistance.application.model.IncidentResolutionPlanDraft;
import com.acme.coldtrace.platform.aiassistance.application.ports.AiStructuredOutputPort;
import com.acme.coldtrace.platform.aiassistance.domain.model.commands.GenerateComplianceSummaryCommand;
import com.acme.coldtrace.platform.aiassistance.domain.model.commands.GenerateDashboardInterpretationCommand;
import com.acme.coldtrace.platform.aiassistance.domain.model.commands.GenerateIncidentResolutionPlanDraftCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Application service that owns AI prompt intent and delegates provider calls.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class AiAssistanceCommandServiceImpl implements AiAssistanceCommandService {
    private static final String SYSTEM_INSTRUCTION = """
            You are ColdTrace's backend AI assistant. Generate advisory operational guidance only.
            Do not claim that an incident is resolved, closed, escalated, or mutated.
            Use only the provided backend context. If context is missing, state uncertainty in uncertaintyNotes.
            Return one JSON object only. Do not wrap the response in Markdown, code fences, or explanatory text.
            """;

    private static final String INCIDENT_PLAN_TEMPLATE = """
            Generate an advisory resolution plan for this ColdTrace incident.

            Backend incident context:
            {incidentContext}

            Requirements:
            - Include summary, probable cause, ordered recommended steps, corrective-action draft,
              resolution-notes draft, escalation recommendation, required evidence, and uncertainty notes.
            - Keep all recommendations advisory until an operator approves them.
            - Keep summary and probableCause under 220 characters.
            - Return 3 recommendedSteps, 3 requiredEvidence items, and 1 to 3 uncertaintyNotes items.
            - Keep each step field, evidence item, and uncertainty note under 160 characters.

            {format}
            """;

    private static final String DASHBOARD_INTERPRETATION_TEMPLATE = """
            Interpret this ColdTrace dashboard context for an operations user.

            Response language: {responseLanguage}

            Backend dashboard context:
            {dashboardContext}

            Requirements:
            - Include summary, attentionLevel, metric-level insights, risks, recommended actions, and uncertainty notes.
            - The attentionLevel must be one concise operational label such as stable, attention recommended, or critical attention.
            - Return exactly 3 insights, exactly 2 risks, exactly 2 recommendedActions, and exactly 1 uncertaintyNotes item.
            - Keep summary under 240 characters.
            - Keep every insight title under 50 characters and every insight interpretation under 180 characters.
            - Keep every risk, recommendedAction, and uncertaintyNote under 160 characters.
            - Keep every string short and operational; use one sentence per item.
            - Write every user-facing string value in the response language above.
            - If the response language is Spanish, do not answer in English; translate summary, attentionLevel, insight titles, insight interpretations, risks, recommendedActions, and uncertaintyNotes to Spanish.
            - Keep JSON property names unchanged.
            - Do not invent source data that is absent from the context.

            {format}
            """;

    private static final String COMPLIANCE_SUMMARY_TEMPLATE = """
            Generate an advisory compliance summary from this ColdTrace report context.

            Backend report context:
            {reportContext}

            Requirements:
            - Include executive summary, findings, evidence gaps, recommended actions, and uncertainty notes.
            - Return exactly 3 findings, exactly 2 evidenceGaps, exactly 2 recommendedActions, and exactly 1 uncertaintyNotes item.
            - Never return an empty array for findings, evidenceGaps, recommendedActions, or uncertaintyNotes.
            - Keep executiveSummary under 260 characters.
            - Keep each finding field, evidence gap, recommended action, and uncertainty note under 160 characters.
            - If detailed evidence is limited, create conservative advisory items from the persisted report metrics and state the limitation in evidenceGaps or uncertaintyNotes.
            - Do not present advisory output as a regulator-approved conclusion.

            {format}
            """;

    private final AiStructuredOutputPort aiStructuredOutputPort;

    public AiAssistanceCommandServiceImpl(AiStructuredOutputPort aiStructuredOutputPort) {
        this.aiStructuredOutputPort = aiStructuredOutputPort;
    }

    @Override
    public Result<AiGeneratedResponse<IncidentResolutionPlanDraft>, AiAssistanceFailure> handle(
            GenerateIncidentResolutionPlanDraftCommand command) {
        log.debug("Generating AI incident resolution plan draft");
        return aiStructuredOutputPort.requestStructuredOutput(
                new AiStructuredPrompt(
                        SYSTEM_INSTRUCTION,
                        INCIDENT_PLAN_TEMPLATE,
                        Map.of("incidentContext", command.incidentContext())
                ),
                IncidentResolutionPlanDraft.class
        );
    }

    @Override
    public Result<AiGeneratedResponse<DashboardInterpretationDraft>, AiAssistanceFailure> handle(
            GenerateDashboardInterpretationCommand command) {
        log.debug("Generating AI dashboard interpretation draft");
        return aiStructuredOutputPort.requestStructuredOutput(
                new AiStructuredPrompt(
                        SYSTEM_INSTRUCTION,
                        DASHBOARD_INTERPRETATION_TEMPLATE,
                        Map.of(
                                "dashboardContext", command.dashboardContext(),
                                "responseLanguage", command.responseLanguage()
                        )
                ),
                DashboardInterpretationDraft.class
        );
    }

    @Override
    public Result<AiGeneratedResponse<ComplianceSummaryDraft>, AiAssistanceFailure> handle(
            GenerateComplianceSummaryCommand command) {
        log.debug("Generating AI compliance summary draft");
        return aiStructuredOutputPort.requestStructuredOutput(
                new AiStructuredPrompt(
                        SYSTEM_INSTRUCTION,
                        COMPLIANCE_SUMMARY_TEMPLATE,
                        Map.of("reportContext", command.reportContext())
                ),
                ComplianceSummaryDraft.class
        );
    }
}
