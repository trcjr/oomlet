{{- define "oomlet.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{- define "oomlet.fullname" -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- printf "%s" $name | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{- define "oomlet.labels" -}}
app.kubernetes.io/name: {{ include "oomlet.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "oomlet.selectorLabels" -}}
app.kubernetes.io/name: {{ include "oomlet.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "oomlet.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "oomlet.fullname" .) .Values.serviceAccount.name -}}
{{- else -}}
{{- .Values.serviceAccount.name -}}
{{- end -}}
{{- end }}
