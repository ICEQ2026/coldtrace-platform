workspace "ColdTrace Platform Solution - C4 Model" "System-wide software architecture for the ColdTrace cold-chain monitoring solution." {

    model {
        operationsManager = person "Operations Manager" "Monitors assets, reviews alerts, and coordinates corrective actions."
        qualityOwner = person "Quality Owner" "Reviews compliance reports and audit evidence."
        administrator = person "Organization Administrator" "Configures organization users, roles, plans, and operational settings."
        technician = person "Technician" "Executes maintenance and technical service work."

        stripe = softwareSystem "Stripe" "Processes subscription checkout, billing portal, and webhook events." {
            tags "External System"
        }

        aiProvider = softwareSystem "AI Model Provider" "Generates advisory incident plans and dashboard/report interpretations." {
            tags "External System"
        }

        googleIdentity = softwareSystem "Google Identity" "Authenticates users with Google through OAuth 2.0 / OpenID Connect." {
            tags "External System"
        }

        appleIdentity = softwareSystem "Apple Identity" "Authenticates users with Sign in with Apple and identity tokens." {
            tags "External System"
        }

        cloudRun = softwareSystem "Google Cloud Run" "Hosts the production ColdTrace backend container." {
            tags "External System"
        }

        solution = softwareSystem "ColdTrace Solution" "Cold-chain monitoring and compliance platform." {
            landingPage = container "ColdTrace Landing Page" "Presents the product and subscription plans." "HTML, CSS, JavaScript" {
                tags "Directory"
            }

            webApp = container "ColdTrace Web Application" "Provides operational dashboards, asset management, incidents, reports, maintenance, AI assistance, and billing flows." "Angular and TypeScript" {
                tags "Web Browser"
            }

            api = container "ColdTrace Platform REST API" "Provides organization-scoped REST APIs for identity access, asset management, monitoring, alerts, reports, maintenance, AI assistance, and billing." "Java and Spring Boot" {
                tags "Server-side Application"
                identityAccessModule = component "Identity Access Module" "Organizations, users, roles, organization sign-up, planned JWT authentication, and external identity links."
                assetManagementModule = component "Asset Management Module" "Locations, gateways, assets, IoT devices, and asset settings."
                monitoringModule = component "Monitoring Module" "Sensor readings and backend-owned demo telemetry generation."
                alertsModule = component "Alerts Module" "Incidents, notifications, acknowledgement, escalation, corrective action, and resolution."
                reportsModule = component "Reports Module" "Operational and compliance report generation."
                maintenanceModule = component "Maintenance Management Module" "Preventive maintenance schedules and technical service requests."
                sharedModule = component "Shared Module" "Application results, localized REST errors, JPA base support, naming strategy, CORS, and OpenAPI configuration."
            }

            database = container "ColdTrace Database" "Stores organization, asset, monitoring, incident, report, maintenance, subscription, and AI-assistance data." "MySQL / Cloud SQL" {
                tags "Database"
            }
        }

        operationsManager -> webApp "Uses" "HTTPS"
        qualityOwner -> webApp "Uses" "HTTPS"
        administrator -> webApp "Uses" "HTTPS"
        technician -> webApp "Uses" "HTTPS"
        operationsManager -> landingPage "Visits" "HTTPS"
        administrator -> landingPage "Reviews pricing" "HTTPS"

        webApp -> api "Calls REST APIs" "JSON/HTTPS"
        landingPage -> api "Reads pricing and starts registration or upgrade flows" "JSON/HTTPS"
        api -> database "Reads and writes" "JDBC"
        api -> stripe "Creates checkout/customer portal sessions and processes webhook state" "HTTPS"
        api -> aiProvider "Requests advisory structured AI output" "HTTPS"
        api -> googleIdentity "Validates Google identity and exchanges OAuth/OIDC responses" "HTTPS"
        api -> appleIdentity "Validates Apple identity and exchanges authorization responses" "HTTPS"
        cloudRun -> api "Runs containerized backend" "Container runtime"

        identityAccessModule -> sharedModule "Uses shared abstractions"
        assetManagementModule -> sharedModule "Uses shared abstractions"
        monitoringModule -> sharedModule "Uses shared abstractions"
        alertsModule -> sharedModule "Uses shared abstractions"
        reportsModule -> sharedModule "Uses shared abstractions"
        maintenanceModule -> sharedModule "Uses shared abstractions"

        assetManagementModule -> identityAccessModule "Validates organization and user references through application services"
        monitoringModule -> assetManagementModule "Validates assets, devices, gateways, and effective settings"
        alertsModule -> assetManagementModule "Validates affected asset/device context"
        alertsModule -> monitoringModule "References readings that triggered incidents"
        reportsModule -> monitoringModule "Aggregates readings and operational metrics"
        reportsModule -> alertsModule "Aggregates incident and corrective-action metrics"
        maintenanceModule -> assetManagementModule "Schedules work for assets"
        maintenanceModule -> alertsModule "Links service requests to incidents when applicable"
    }

    views {
        systemContext solution "SystemContext" "Whole-solution context with users and external systems." {
            include operationsManager
            include qualityOwner
            include administrator
            include technician
            include solution
            include stripe
            include aiProvider
            include googleIdentity
            include appleIdentity
            include cloudRun
            autoLayout lr
        }

        container solution "ContainerView" "Container view for the ColdTrace Solution." {
            include operationsManager
            include qualityOwner
            include administrator
            include technician
            include landingPage
            include webApp
            include api
            include database
            include stripe
            include aiProvider
            include googleIdentity
            include appleIdentity
            include cloudRun
            autoLayout lr
        }

        component api "ColdTraceApiComponents" "Component view of the ColdTrace Platform REST API." {
            include identityAccessModule
            include assetManagementModule
            include monitoringModule
            include alertsModule
            include reportsModule
            include maintenanceModule
            include sharedModule
            include database
            include stripe
            include aiProvider
            include googleIdentity
            include appleIdentity
            include webApp
            autoLayout lr
        }

        styles {
            element "Person" {
                shape Person
                background "#ffffff"
                color #0773af
                stroke #0773af
                fontSize 22
            }

            element "Software System" {
                background "#ffffff"
                color #0773af
                stroke #0773af
            }

            element "External System" {
                background "#999999"
                color "#ffffff"
            }

            element "Container" {
                background "#438dd5"
                color "#ffffff"
            }

            element "Server-side Application" {
                background "#ffffff"
                color #0773af
                stroke #0773af
                strokeWidth 7
                shape Shell
            }

            element "Directory" {
                shape Folder
                background "#ffffff"
                color #0773af
                stroke #0773af
            }

            element "Web Browser" {
                shape WebBrowser
                background "#ffffff"
                color #0773af
                stroke #0773af
            }

            element "Database" {
                shape Cylinder
                background "#ffffff"
                color #0773af
                stroke #0773af
            }

            element "Component" {
                background "#85bbf0"
                color "#000000"
            }
        }

        theme default
    }

    properties {
        structurizr.groupSeparator "/"
    }
}
