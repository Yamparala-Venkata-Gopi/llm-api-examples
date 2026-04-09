{{/*
Expand the name of the chart.
*/}}
{{- define "llm-api-app.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Fully qualified resource name (Deployment, Service, Secret prefix).
Use fullnameOverride when installing multiple releases in one namespace (e.g. python + java).
*/}}
{{- define "llm-api-app.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s" $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "llm-api-app.labels" -}}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
{{ include "llm-api-app.selectorLabels" . }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels — must stay stable across helm upgrades (e.g. python ↔ java).
Do not add .Values.app here: Deployment spec.selector is immutable in Kubernetes.
*/}}
{{- define "llm-api-app.selectorLabels" -}}
app.kubernetes.io/name: {{ include "llm-api-app.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Optional pod labels (not used in selectors) for filtering / observability.
*/}}
{{- define "llm-api-app.podExtraLabels" -}}
app: {{ .Values.app }}
{{- end }}
