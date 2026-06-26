package com.acme.coldtrace.platform.alerts.application.internal.commandservices;

import com.acme.coldtrace.platform.alerts.application.commandservices.AiResolutionPlanCommandFailure;
import com.acme.coldtrace.platform.alerts.application.commandservices.AiResolutionPlanCommandService;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.AiResolutionPlan;
import com.acme.coldtrace.platform.alerts.domain.model.commands.ApproveAiResolutionPlanCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.CreateAiResolutionPlanCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.RejectAiResolutionPlanCommand;
import com.acme.coldtrace.platform.alerts.domain.repositories.AiResolutionPlanRepository;
import com.acme.coldtrace.platform.alerts.domain.repositories.IncidentRepository;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Application service implementation for AI resolution plan lifecycle commands.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class AiResolutionPlanCommandServiceImpl implements AiResolutionPlanCommandService {
    private final AiResolutionPlanRepository aiResolutionPlanRepository;
    private final IncidentRepository incidentRepository;
    private final IamContextFacade iamContextFacade;

    public AiResolutionPlanCommandServiceImpl(
            AiResolutionPlanRepository aiResolutionPlanRepository,
            IncidentRepository incidentRepository,
            IamContextFacade iamContextFacade
    ) {
        this.aiResolutionPlanRepository = aiResolutionPlanRepository;
        this.incidentRepository = incidentRepository;
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Persists generated advisory content as a pending plan.
     *
     * @param command command containing generated content
     * @return success with persisted plan or failure with command error
     */
    @Override
    @Transactional
    public Result<AiResolutionPlan, AiResolutionPlanCommandFailure> handle(CreateAiResolutionPlanCommand command) {
        var scopeFailure = validateScope(command.organizationId(), command.incidentId());
        if (scopeFailure.isPresent()) {
            return Result.failure(scopeFailure.get());
        }

        var plan = aiResolutionPlanRepository.save(new AiResolutionPlan(command));
        log.info("AI resolution plan created: id={}, organizationId={}, incidentId={}",
                plan.getId(), plan.getOrganizationId(), plan.getIncidentId());
        return Result.success(plan);
    }

    /**
     * Approves a pending plan and stores final operator-edited fields.
     *
     * @param command approval command
     * @return success with updated plan or failure with command error
     */
    @Override
    @Transactional
    public Result<AiResolutionPlan, AiResolutionPlanCommandFailure> handle(ApproveAiResolutionPlanCommand command) {
        var plan = findPendingPlan(command.organizationId(), command.incidentId(), command.planId());
        if (plan.isFailure()) {
            return plan;
        }

        var pendingPlan = plan.success().orElseThrow();
        pendingPlan.approve(command);
        var approvedPlan = aiResolutionPlanRepository.save(pendingPlan);
        log.info("AI resolution plan approved: id={}, organizationId={}, incidentId={}",
                approvedPlan.getId(), approvedPlan.getOrganizationId(), approvedPlan.getIncidentId());
        return Result.success(approvedPlan);
    }

    /**
     * Rejects a pending plan and stores audit metadata.
     *
     * @param command rejection command
     * @return success with updated plan or failure with command error
     */
    @Override
    @Transactional
    public Result<AiResolutionPlan, AiResolutionPlanCommandFailure> handle(RejectAiResolutionPlanCommand command) {
        var plan = findPendingPlan(command.organizationId(), command.incidentId(), command.planId());
        if (plan.isFailure()) {
            return plan;
        }

        var pendingPlan = plan.success().orElseThrow();
        pendingPlan.reject(command);
        var rejectedPlan = aiResolutionPlanRepository.save(pendingPlan);
        log.info("AI resolution plan rejected: id={}, organizationId={}, incidentId={}",
                rejectedPlan.getId(), rejectedPlan.getOrganizationId(), rejectedPlan.getIncidentId());
        return Result.success(rejectedPlan);
    }

    private Result<AiResolutionPlan, AiResolutionPlanCommandFailure> findPendingPlan(
            Long organizationId,
            Long incidentId,
            Long planId
    ) {
        var scopeFailure = validateScope(organizationId, incidentId);
        if (scopeFailure.isPresent()) {
            return Result.failure(scopeFailure.get());
        }

        var plan = aiResolutionPlanRepository.findByIdAndIncidentIdAndOrganizationId(
                planId,
                incidentId,
                organizationId
        );
        if (plan.isEmpty()) {
            log.warn("AI resolution plan not found: organizationId={}, incidentId={}, planId={}",
                    organizationId, incidentId, planId);
            return Result.failure(new AiResolutionPlanCommandFailure.PlanNotFound());
        }
        if (!plan.get().isPending()) {
            log.warn("AI resolution plan already decided: organizationId={}, incidentId={}, planId={}",
                    organizationId, incidentId, planId);
            return Result.failure(new AiResolutionPlanCommandFailure.PlanAlreadyDecided());
        }
        return Result.success(plan.get());
    }

    private Optional<AiResolutionPlanCommandFailure> validateScope(Long organizationId, Long incidentId) {
        if (!iamContextFacade.organizationExists(organizationId)) {
            log.warn("Organization not found for AI resolution plan command: organizationId={}", organizationId);
            return Optional.of(new AiResolutionPlanCommandFailure.OrganizationNotFound());
        }
        if (!incidentRepository.existsByIdAndOrganizationId(incidentId, organizationId)) {
            log.warn("Incident not found for AI resolution plan command: organizationId={}, incidentId={}",
                    organizationId, incidentId);
            return Optional.of(new AiResolutionPlanCommandFailure.IncidentNotFound());
        }
        return Optional.empty();
    }
}
